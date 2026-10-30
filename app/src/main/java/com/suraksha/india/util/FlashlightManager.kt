package com.suraksha.india.util

import android.content.Context
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.*

class FlashlightManager(private val context: Context) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId: String? = null
    private var strobeJob: Job? = null
    var isFlashOn: Boolean = false
        private set

    init {
        try {
            val idList = cameraManager.cameraIdList
            if (idList.isNotEmpty()) {
                cameraId = idList[0]
            }
        } catch (e: Exception) {
            cameraId = null
        }
    }

    fun setTorch(enable: Boolean) {
        val id = cameraId ?: return
        try {
            cameraManager.setTorchMode(id, enable)
            isFlashOn = enable
        } catch (e: Exception) {
            isFlashOn = false
        }
    }

    fun toggleStrobe(scope: CoroutineScope, onStateChange: (Boolean) -> Unit) {
        if (strobeJob?.isActive == true) {
            strobeJob?.cancel()
            setTorch(false)
            onStateChange(false)
        } else {
            onStateChange(true)
            strobeJob = scope.launch(Dispatchers.Default) {
                var state = false
                while (isActive) {
                    state = !state
                    setTorch(state)
                    delay(150)
                }
                setTorch(false)
            }
        }
    }

    fun release() {
        strobeJob?.cancel()
        setTorch(false)
    }
}
