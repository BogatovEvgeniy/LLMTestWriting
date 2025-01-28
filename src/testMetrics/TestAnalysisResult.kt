package testMetrics

import coordinator.TestGenerationConfig

data class TestAnalysisResult(
    val basicMetrics: BasicMetrics,
    val coverageMetrics: CoverageMetrics,
    val qualityMetrics: QualityMetrics,
    val readabilityMetrics: ReadabilityMetrics,
    val config: TestGenerationConfig
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
            if (qualityMetrics.hasDescriptiveNames > 0) quality += 5  // Check if count is greater than 0
            if (qualityMetrics.usesAssertionVariety > 0) quality += 5   // Check if count is greater than 0
            if (qualityMetrics.hasTestDocumentation > 0) quality += 5   // Check if count is greater than 0
            if (qualityMetrics.followsNamingConventions > 0) quality += 5 // Check if count is greater than 0
            return (quality * config.qualityMetricsWeight)
        }

    val readabilityScore: Double
        get() {
            var readability = 0
            if (readabilityMetrics.usesBackticks > 0) readability += 7   // Check if count is greater than 0
            if (readabilityMetrics.hasComments > 0) readability += 7     // Check if count is greater than 0
            if (readabilityMetrics.averageTestLength in 5..25) readability += 6
            return (readability * config.readabilityMetricsWeight)
        }

    val score: Double
        get() = (basicScore + coverageScore + qualityScore + readabilityScore)
}

fun TestAnalysisResult.add(result: TestAnalysisResult) =
    TestAnalysisResult(
        this.basicMetrics.add(result.basicMetrics),
        this.coverageMetrics.add(result.coverageMetrics),
        this.qualityMetrics.add(result.qualityMetrics), // Pass totalFiles
        this.readabilityMetrics.add(result.readabilityMetrics), // Pass totalFiles
        this.config
    )

// In TestAnalysisResult class
fun TestAnalysisResult.applyThreshold(totalFiles: Int): TestAnalysisResult {
    return TestAnalysisResult(
        basicMetrics = this.basicMetrics
        ,coverageMetrics = this.coverageMetrics//.applyThreshold(totalFiles)
        ,qualityMetrics = this.qualityMetrics.applyThreshold(totalFiles)
        ,readabilityMetrics = this.readabilityMetrics.applyThreshold(totalFiles)
        ,config = this.config
    )
}