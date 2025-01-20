package coordinator

data class TestGenerationConfig(
    val basicMetricsWeight: Double,
    val coverageMetricsWeight: Double,
    val qualityMetricsWeight: Double,
    val readabilityMetricsWeight: Double,
    val timingMetricsWeight: Double,

    val useGPT: Boolean,
    val useGemini: Boolean,
    val useCodeLlama: Boolean,

    val prompt: String
)