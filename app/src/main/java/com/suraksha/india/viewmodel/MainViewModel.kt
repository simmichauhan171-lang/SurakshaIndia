package com.suraksha.india.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.suraksha.india.data.AppDatabase
import com.suraksha.india.data.ContactEntity
import com.suraksha.india.data.ContactRepository
import com.suraksha.india.model.Helpline
import com.suraksha.india.model.SafetyGuide
import com.suraksha.india.util.FlashlightManager
import com.suraksha.india.util.LocationServiceHelper
import com.suraksha.india.util.SmsDispatcherHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ContactRepository
    val contacts: StateFlow<List<ContactEntity>>

    val flashlightManager = FlashlightManager(application)
    val isStrobeActive = MutableStateFlow(false)

    val latitude = MutableStateFlow<Double?>(null)
    val longitude = MutableStateFlow<Double?>(null)
    val isLocationFetching = MutableStateFlow(false)
    val sosStatusMessage = MutableStateFlow<String?>(null)

    val helplines = listOf(
        Helpline("National Emergency", "112", "Single universal number for Police, Fire, and Ambulance", "Emergency"),
        Helpline("Women Helpline (All India)", "1091", "24/7 emergency response for women in distress", "Women"),
        Helpline("Women Helpline (Domestic Violence)", "181", "Support against domestic violence and abuse", "Women"),
        Helpline("Cyber Crime Helpline", "1930", "Immediate financial fraud and cyber harassment reporting", "Cyber"),
        Helpline("Childline India", "1098", "Dedicated emergency response for children in need", "Child"),
        Helpline("Police Control Room", "100", "State police emergency response", "Police"),
        Helpline("Ambulance Service", "108", "Medical emergencies and trauma rescue", "Medical")
    )

    val safetyGuides = listOf(
        SafetyGuide(
            id = "g1",
            title = "Commuting & Cab Safety Protocol",
            summary = "Key verification points before and during public transport journeys.",
            steps = listOf(
                "Always check that child locks are disabled before closing the door.",
                "Share live location link with your trusted contact.",
                "Verify vehicle plate number matches the booking application.",
                "If the vehicle deviates from course, demand to stop at a well-lit establishment."
            )
        ),
        SafetyGuide(
            id = "g2",
            title = "Getting to Safety",
            summary = "Practical steps to reduce risk and get help quickly.",
            steps = listOf(
                "Move toward a busy, well-lit public place or a staffed shop.",
                "Call 112 or ask a nearby trusted adult or staff member to call.",
                "Use a clear loud voice to attract attention and say what help you need.",
                "Leave the area when you can; avoid confronting or chasing the person."
            )
        ),
        SafetyGuide(
            id = "g3",
            title = "Legal Rights: Zero FIR & Arrest Provisions",
            summary = "Essential legal protections under Indian law.",
            steps = listOf(
                "Zero FIR: Any police station must register an FIR irrespective of jurisdiction.",
                "Arrest rules and legal provisions can change. Confirm current rules with an official legal-aid service or qualified lawyer before relying on this guide.",
                "Right to Free Legal Aid: Indigent women are entitled to state legal services.",
                "Right to Privacy: A woman survivor has the right to record her statement privately before a female magistrate."
            )
        ),
        SafetyGuide(
            id = "g4",
            title = "Offline Emergency First-Aid",
            summary = "Immediate care actions during injuries and panic episodes.",
            steps = listOf(
                "Severe Bleeding: Apply constant direct pressure with a clean cloth. Do not remove saturated layers.",
                "Fainting: Lay the person flat, elevate legs 12 inches, and ensure unimpeded airflow.",
                "Acute Panic: Execute 4-4-4-4 box breathing (inhale 4s, hold 4s, exhale 4s, pause 4s)."
            )
        )
    )

    init {
        val dao = AppDatabase.getDatabase(application).contactDao()
        repository = ContactRepository(dao)
        contacts = repository.allContacts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        refreshLocation()
    }

    fun refreshLocation() {
        viewModelScope.launch {
            isLocationFetching.value = true
            val loc = LocationServiceHelper.fetchCurrentLocation(getApplication())
            if (loc != null) {
                latitude.value = loc.latitude
                longitude.value = loc.longitude
            }
            isLocationFetching.value = false
        }
    }

    fun dispatchSOSAlert(onFinished: (SmsDispatcherHelper.DispatchResult) -> Unit) {
        viewModelScope.launch {
            isLocationFetching.value = true
            val loc = LocationServiceHelper.fetchCurrentLocation(getApplication())
            if (loc != null) {
                latitude.value = loc.latitude
                longitude.value = loc.longitude
            }
            isLocationFetching.value = false

            val result = SmsDispatcherHelper.sendEmergencyAlerts(
                getApplication(),
                contacts.value,
                latitude.value,
                longitude.value
            )

            when (result) {
                is SmsDispatcherHelper.DispatchResult.Submitted -> {
                    sosStatusMessage.value = "SOS SMS request submitted for ${result.count} contact(s). Delivery is not confirmed; call 112 if immediate help is needed."
                }
                is SmsDispatcherHelper.DispatchResult.PartialFailure -> {
                    sosStatusMessage.value = "Sent: ${result.sent}, Failed: ${result.failed}."
                }
                is SmsDispatcherHelper.DispatchResult.Failure -> {
                    sosStatusMessage.value = result.reason
                }
            }
            onFinished(result)
        }
    }

    fun addContact(name: String, phoneNumber: String, isPrimary: Boolean) {
        viewModelScope.launch {
            val contact = ContactEntity(
                name = name.trim(),
                phoneNumber = phoneNumber.trim(),
                isPrimary = isPrimary
            )
            val newId = repository.insertContact(contact)
            if (isPrimary) {
                repository.makePrimary(newId)
            }
        }
    }

    fun updateContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.updateContact(contact)
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    fun setPrimaryContact(id: Long) {
        viewModelScope.launch {
            repository.makePrimary(id)
        }
    }

    fun toggleStrobe() {
        flashlightManager.toggleStrobe(viewModelScope) { active ->
            isStrobeActive.value = active
        }
    }

    override fun onCleared() {
        super.onCleared()
        flashlightManager.release()
    }
}
