package com.example.hardware

enum class HardwareBackend(
    val title: String,
    val subtitle: String,
    val description: String,
    val tag: String
) {
    CPU_NEON(
        title = "ARM NEON CPU",
        subtitle = "ARMv8/v9 SIMD Multi-Core",
        description = "Runs quantized tensor operations across high-performance CPU cores with NEON SIMD vectorization.",
        tag = "CPU-NEON"
    ),
    GPU_VULKAN(
        title = "Vulkan Compute GPU",
        subtitle = "Adreno / Mali Compute Shaders",
        description = "Offloads transformer layers directly to mobile GPU shaders for maximum token throughput and parallelism.",
        tag = "GPU-VULKAN"
    ),
    NPU_NNAPI(
        title = "Android NNAPI / NPU",
        subtitle = "Neural Processing Silicon",
        description = "Utilizes Android Neural Networks API to target dedicated NPU, Hexagon DSP, and Tensor processing units.",
        tag = "NPU-NNAPI"
    ),
    HYBRID_CORE(
        title = "Hybrid CPU + GPU",
        subtitle = "Dynamic Layer Splitting",
        description = "Distributes attention layers to GPU VRAM while maintaining KV cache and feed-forward operations in CPU RAM.",
        tag = "HYBRID"
    )
}

data class CpuCoreInfo(
    val coreIndex: Int,
    val frequencyMhz: Int,
    val loadPercentage: Float,
    val isPerformanceCore: Boolean
)

data class HardwareSpecs(
    val deviceModel: String,
    val socManufacturer: String,
    val socModel: String,
    val cpuArch: String,
    val totalCores: Int,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val gpuRenderer: String,
    val vulkanSupported: Boolean,
    val nnapiAvailable: Boolean,
    val neonSimdSupported: Boolean,
    val fp16ComputeSupported: Boolean,
    val dotProdSupported: Boolean
)

data class HardwareRealtimeStats(
    val batteryTempCelsius: Float = 34.2f,
    val thermalStatus: String = "Normal (Optimal)",
    val cpuUsagePercent: Float = 28.5f,
    val ramUsedMb: Long = 2650,
    val ramTotalMb: Long = 8192,
    val ramFreeMb: Long = 5542,
    val coreStats: List<CpuCoreInfo> = emptyList(),
    val gpuLoadPercent: Float = 42.0f,
    val activeBackend: HardwareBackend = HardwareBackend.GPU_VULKAN,
    val currentGflops: Float = 145.8f
)

data class HardwareBenchmarkResult(
    val timestamp: Long = System.currentTimeMillis(),
    val backend: HardwareBackend,
    val matrixDimension: Int,
    val operationsCount: Long,
    val totalTimeMs: Long,
    val gflops: Float,
    val int8Tops: Float,
    val memoryBandwidthGbps: Float,
    val cacheLatencyNs: Float,
    val initialTempCelsius: Float,
    val peakTempCelsius: Float,
    val score: Int
)

data class HardwareStressTestProgress(
    val isRunning: Boolean = false,
    val currentPhase: String = "Idle",
    val progress: Float = 0f,
    val currentGflops: Float = 0f,
    val currentBandwidthGbps: Float = 0f,
    val latestResult: HardwareBenchmarkResult? = null
)
