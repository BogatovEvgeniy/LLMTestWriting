package testMetrics.timing

data class TimingMetrics(
    val totalGenerationTimeMs: Long,
    val promptGenerationTimeMs: Long,
    val apiCallTimeMs: Long,
    //val perFileGenerationTimes: Map<String, Long>,
    val averageFileGenerationTimeMs: Double,
    val parsingTimeMs: Long,
    val testAnalysisTimeMs: Long
) {
    val totalProcessingTimeMs: Long = totalGenerationTimeMs + testAnalysisTimeMs
}

fun TimingMetrics.add(result: TimingMetrics) =
    TimingMetrics(
        this.totalGenerationTimeMs + result.totalGenerationTimeMs,
        this.promptGenerationTimeMs + result.promptGenerationTimeMs,
        this.apiCallTimeMs + result.apiCallTimeMs,
        this.averageFileGenerationTimeMs + result.averageFileGenerationTimeMs,
        this.parsingTimeMs + result.parsingTimeMs,
        this.testAnalysisTimeMs + result.testAnalysisTimeMs
    )
