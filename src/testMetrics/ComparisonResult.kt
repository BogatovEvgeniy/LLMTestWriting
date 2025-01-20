package testMetrics

import generator.Generators
import testMetrics.timing.TimingMetrics

data class ComparisonResult(
    val generator: Generators,
    val analysis: TestAnalysisResult,
    val timestamp: Long,
    val timingMetrics: TimingMetrics,
    val scores: TestScores
) {
    constructor(
        generator: Generators,
        analysis: TestAnalysisResult,
        timestamp: Long,
        timingMetrics: TimingMetrics
    ) : this(
        generator = generator,
        analysis = analysis,
        timestamp = timestamp,
        timingMetrics = timingMetrics,
        scores = TestScores(
            totalScore = analysis.score,
            basicScore = analysis.basicScore,
            coverageScore = analysis.coverageScore,
            qualityScore = analysis.qualityScore,
            readabilityScore = analysis.readabilityScore
        )
    )
}