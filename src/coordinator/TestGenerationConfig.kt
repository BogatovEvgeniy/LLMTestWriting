package coordinator

data class TestGenerationConfig(
    val basicMetricsWeight: Double = 0.25,
    val coverageMetricsWeight: Double = 0.25,
    val qualityMetricsWeight: Double = 0.25,
    val readabilityMetricsWeight: Double = 0.25,

    val useGPT: Boolean = false,
    val useGemini: Boolean = false,
    val useCodeLlama: Boolean = false,

    val prompt: String = "Provide me a set of unit test for the provided codebase"
)