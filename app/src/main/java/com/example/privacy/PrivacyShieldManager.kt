package com.example.privacy

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PrivacyTelemetry(
    val isOfflineModeEnforced: Boolean = true,
    val outboundNetworkBlocked: Boolean = true,
    val bytesTransmittedExternal: Long = 0,
    val isLocalEncryptionActive: Boolean = true,
    val ramUsedMb: Int = 2450,
    val ramTotalMb: Int = 8192,
    val batteryTempCelsius: Float = 34.5f,
    val gpuVulkanActive: Boolean = true,
    val cpuThreadCount: Int = 6,
    val autoCompileCode: Boolean = true
)

object PrivacyShieldManager {

    private val _telemetry = MutableStateFlow(PrivacyTelemetry())
    val telemetry: StateFlow<PrivacyTelemetry> = _telemetry.asStateFlow()

    fun toggleOfflineMode(enforce: Boolean) {
        _telemetry.value = _telemetry.value.copy(
            isOfflineModeEnforced = enforce,
            outboundNetworkBlocked = enforce
        )
    }

    fun toggleAutoCompile(enable: Boolean) {
        _telemetry.value = _telemetry.value.copy(autoCompileCode = enable)
    }

    fun setCpuThreads(threads: Int) {
        _telemetry.value = _telemetry.value.copy(cpuThreadCount = threads)
    }

    fun toggleGpuVulkan(active: Boolean) {
        _telemetry.value = _telemetry.value.copy(gpuVulkanActive = active)
    }
}
