package testMetrics

data class TestAnalysisResult(
    val basicMetrics: BasicMetrics,
    val coverageMetrics: CoverageMetrics,
    val qualityMetrics: QualityMetrics,
    val readabilityMetrics: ReadabilityMetrics
) {
    val score: Int get() {
        var score = 0

        // Basic metrics (30 points)
        score += (basicMetrics.totalTests * 3).coerceAtMost(15)
        score += (basicMetrics.averageAssertionsPerTest * 10).toInt().coerceAtMost(15)

        // Coverage metrics (30 points)
        score += (coverageMetrics.methodsCovered.size * 10).coerceAtMost(20)
        score += coverageMetrics.edgeCasesCovered.count { it.covered } * 2
        score += (coverageMetrics.boundaryTests.size * 2).coerceAtMost(6)

        // Quality metrics (20 points)
        if (qualityMetrics.hasDescriptiveNames) score += 5
        if (qualityMetrics.usesAssertionVariety) score += 5
        if (qualityMetrics.hasTestDocumentation) score += 5
        if (qualityMetrics.followsNamingConventions) score += 5

        // Readability metrics (20 points)
        if (readabilityMetrics.usesBackticks) score += 7
        if (readabilityMetrics.hasComments) score += 7
        if (readabilityMetrics.averageTestLength in 5..25) score += 6

        return score.coerceIn(0, 100)
    }
}