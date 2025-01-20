package testMetrics

data class TestScores(
    val totalScore: Double,
    val basicScore: Double,
    val coverageScore: Double,
    val qualityScore: Double,
    val readabilityScore: Double
)