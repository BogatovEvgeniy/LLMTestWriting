package testMetrics.timing

class TimingCollector {
    private var startTime: Long = 0
    private val timeMarkers = mutableMapOf<String, Long>()
    private val perFileTimings = mutableMapOf<String, Long>()

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun recordFileGeneration(fileName: String, duration: Long) {
        perFileTimings[fileName] = duration
    }

    fun markTime(marker: String) {
        timeMarkers[marker] = System.currentTimeMillis()
    }

    fun getTimingMetrics(): TimingMetrics {
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime

        // Calculate average file generation time only if we have files
        val avgFileTime = if (perFileTimings.isNotEmpty()) {
            perFileTimings.values.average()
        } else {
            0.0
        }

        return TimingMetrics(
            totalGenerationTimeMs = totalTime,
            promptGenerationTimeMs = getDuration("prompt_start", "prompt_end"),
            apiCallTimeMs = getDuration("api_start", "api_end"),
            perFileGenerationTimes = perFileTimings.toMap(),
            averageFileGenerationTimeMs = avgFileTime,
            parsingTimeMs = getDuration("parsing_start", "parsing_end"),
            testAnalysisTimeMs = getDuration("analysis_start", "analysis_end")
        )
    }

    private fun getDuration(startMarker: String, endMarker: String): Long {
        val startTime = timeMarkers[startMarker] ?: return 0
        val endTime = timeMarkers[endMarker] ?: return 0
        return endTime - startTime
    }
}