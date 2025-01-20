package testMetrics

import coordinator.TestGenerationConfig

data class TestAnalysisResult(
    val basicMetrics: BasicMetrics,
    val coverageMetrics: CoverageMetrics,
    val qualityMetrics: QualityMetrics,
    val readabilityMetrics: ReadabilityMetrics,
    private val config: TestGenerationConfig
) {
    val basicScore: Double
        get() {
            var basic = 0
            basic += (basicMetrics.totalTests * 3)
            basic += (basicMetrics.averageAssertionsPerTest * 10).toInt()
            return (basic * config.basicMetricsWeight)
        }

    val coverageScore: Double
        get() {
            var coverage = 0
            coverage += (coverageMetrics.methodsCovered * 10)
            coverage += coverageMetrics.edgeCasesCovered.count { it.covered } * 2
            coverage += (coverageMetrics.boundaryTests.size * 2)
            return (coverage * config.coverageMetricsWeight)
        }

    val qualityScore: Double
        get() {
            var quality = 0
            if (qualityMetrics.hasDescriptiveNames) quality += 5
            if (qualityMetrics.usesAssertionVariety) quality += 5
            if (qualityMetrics.hasTestDocumentation) quality += 5
            if (qualityMetrics.followsNamingConventions) quality += 5
            return (quality * config.qualityMetricsWeight)
        }

    val readabilityScore: Double
        get() {
            var readability = 0
            if (readabilityMetrics.usesBackticks) readability += 7
            if (readabilityMetrics.hasComments) readability += 7
            if (readabilityMetrics.averageTestLength in 5..25) readability += 6
            return (readability * config.readabilityMetricsWeight)
        }

    val score: Double
        get() = (basicScore + coverageScore + qualityScore + readabilityScore)
}