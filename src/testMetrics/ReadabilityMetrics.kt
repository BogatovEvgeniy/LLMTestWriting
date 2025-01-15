package testMetrics

data class ReadabilityMetrics(
    val usesBackticks: Boolean,
    val hasComments: Boolean,
    val averageTestLength: Int
)