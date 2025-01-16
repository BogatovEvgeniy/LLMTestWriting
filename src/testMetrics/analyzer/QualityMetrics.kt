package testMetrics.analyzer

data class QualityMetrics(
    val hasDescriptiveNames: Boolean,
    val usesAssertionVariety: Boolean,
    val hasTestDocumentation: Boolean,
    val followsNamingConventions: Boolean,
    override val score: Double = calculateScore(
        hasDescriptiveNames,
        usesAssertionVariety,
        hasTestDocumentation,
        followsNamingConventions
    )
) : Metrics {
    companion object {
        private fun calculateScore(
            hasDescriptiveNames: Boolean,
            usesAssertionVariety: Boolean,
            hasTestDocumentation: Boolean,
            followsNamingConventions: Boolean
        ): Double {
            var score = 0.0
            if (hasDescriptiveNames) score += 25.0
            if (usesAssertionVariety) score += 25.0
            if (hasTestDocumentation) score += 25.0
            if (followsNamingConventions) score += 25.0
            return score
        }
    }
}