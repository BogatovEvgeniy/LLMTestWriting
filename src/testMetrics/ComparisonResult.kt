package testMetrics

import generator.Generators
import testMetrics.timing.TimingMetrics

data class ComparisonResult(
    val generator: Generators,
    val analysis: TestAnalysisResult,
    val timestamp: Long,
    val timingMetrics: TimingMetrics
)