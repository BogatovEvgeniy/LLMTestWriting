package testMetrics.analyzer

data class EdgeCase(
    val type: String,
    val covered: Boolean,
    val description: String? = null
)