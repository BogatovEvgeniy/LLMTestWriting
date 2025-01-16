package testMetrics.util

object TestAnalysisUtils {
    fun countAnnotations(test: String, annotation: String): Int {
        return test.split("\n")
            .count { it.trim().startsWith(annotation) }
    }

    fun countAssertions(test: String): Int {
        return test.split("\n")
            .count { it.contains("assert") }
    }

    // Other utility methods...
}