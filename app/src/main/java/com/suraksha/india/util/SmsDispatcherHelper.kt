package com.suraksha.india.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import com.suraksha.india.data.ContactEntity

object SmsDispatcherHelper {

    sealed class DispatchResult {
        data class Submitted(val count: Int) : DispatchResult()
        data class PartialFailure(val sent: Int, val failed: Int) : DispatchResult()
        data class Failure(val reason: String) : DispatchResult()
    }

    fun sendEmergencyAlerts(
        context: Context,
        contacts: List<ContactEntity>,
        latitude: Double?,
        longitude: Double?
    ): DispatchResult {
        if (contacts.isEmpty()) {
            return DispatchResult.Failure("No trusted contacts saved. Please add emergency contacts.")
        }

        val locationDetails = if (latitude != null && longitude != null) {
            "\nLocation: https://maps.google.com/?q=$latitude,$longitude"
        } else {
            "\nLocation: GPS signal currently unavailable."
        }

        val emergencyMessage = "EMERGENCY SOS! I need immediate help. Sent via Suraksha India App.$locationDetails"

        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        var successfulDispatches = 0
        var failedDispatches = 0

        for (contact in contacts) {
            val sanitizedNumber = contact.phoneNumber.replace("[^0-9+]".toRegex(), "")
            if (sanitizedNumber.isNotBlank()) {
                try {
                    val parts = smsManager.divideMessage(emergencyMessage)
                    smsManager.sendMultipartTextMessage(sanitizedNumber, null, parts, null, null)
                    successfulDispatches++
                } catch (e: Exception) {
                    failedDispatches++
                }
            } else {
                failedDispatches++
            }
        }

        return when {
            successfulDispatches > 0 && failedDispatches == 0 -> DispatchResult.Submitted(successfulDispatches)
            successfulDispatches > 0 && failedDispatches > 0 -> DispatchResult.PartialFailure(successfulDispatches, failedDispatches)
            else -> DispatchResult.Failure("Failed to send emergency SMS messages. Please check network/SIM card.")
        }
    }

    fun initiatePhoneCall(context: Context, phoneNumber: String) {
        val sanitized = phoneNumber.replace("[^0-9+]".toRegex(), "")
        val callIntent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$sanitized")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(callIntent)
        } catch (e: SecurityException) {
            val fallbackDialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$sanitized")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallbackDialIntent)
        }
    }
}
