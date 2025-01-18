package testMetrics.timing

data class TimingMetrics(
    val totalGenerationTimeMs: Long,
    val promptGenerationTimeMs: Long,
    val apiCallTimeMs: Long,
    val perFileGenerationTimes: Map<String, Long>,
    val averageFileGenerationTimeMs: Double,
    val parsingTimeMs: Long,
    val testAnalysisTimeMs: Long
) {
    val totalProcessingTimeMs: Long = totalGenerationTimeMs + testAnalysisTimeMs
}