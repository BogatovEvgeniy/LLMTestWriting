package testMetrics

data class TestScores(
    val totalScore: Double,
    val basicScore: Double,
    val coverageScore: Double,
    val qualityScore: Double,
    val readabilityScore: Double
)

fun TestScores.add(result: TestScores) =
    TestScores(
        this.totalScore + result.totalScore,
        this.basicScore + result.basicScore,
        this.coverageScore + result.coverageScore,
        this.qualityScore + result.qualityScore,
        this.readabilityScore + result.readabilityScore
    )
