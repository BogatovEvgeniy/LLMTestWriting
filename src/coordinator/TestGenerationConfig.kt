package coordinator

data class TestGenerationConfig(
    val basicMetricsWeight: Double = 0.2,
    val coverageMetricsWeight: Double = 0.2,
    val qualityMetricsWeight: Double = 0.2,
    val readabilityMetricsWeight: Double = 0.2,
    val timingMetricsWeight: Double = 0.2,

    val useGPT: Boolean = false,
    val useGemini: Boolean = false,
    val useCodeLlama: Boolean = false,

    val prompt: String = "Provide me a set of unit test for the provided codebase"
)