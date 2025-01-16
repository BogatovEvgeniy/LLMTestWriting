package testMetrics

import generator.Generators

data class ComparisonResult(
    val generator: Generators,
    val analysis: TestAnalysisResult,
    val timestamp: Long = System.currentTimeMillis(),
)