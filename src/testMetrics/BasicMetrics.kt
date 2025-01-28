package testMetrics

data class BasicMetrics(
    val totalTests: Int,
    val totalAssertions: Int,
    val averageAssertionsPerTest: Double
)

fun BasicMetrics.add(result: BasicMetrics) =
    BasicMetrics(
        this.totalTests + result.totalTests,
        this.totalAssertions + result.totalAssertions,
        this.averageAssertionsPerTest + result.averageAssertionsPerTest
    )
