package testMetrics.analyzer

data class BasicMetrics(
    val totalTests: Int,
    val totalAssertions: Int,
    val averageAssertionsPerTest: Double
)