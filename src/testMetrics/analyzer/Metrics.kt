package testMetrics.analyzer

interface Metrics {
    val score: Double
}

data class BasicMetrics(
    val totalTests: Int,
    val totalAssertions: Int,
    val averageAssertionsPerTest: Double,
    override val score: Double
) : Metrics