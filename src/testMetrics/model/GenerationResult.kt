package testMetrics.model

import testMetrics.timing.TimingMetrics

data class GenerationResult(
    val tests: String,
    val timingMetrics: TimingMetrics
)