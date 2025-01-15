package testMetrics

data class QualityMetrics(
    val hasDescriptiveNames: Boolean,
    val usesAssertionVariety: Boolean,
    val hasTestDocumentation: Boolean,
    val followsNamingConventions: Boolean
)