package testMetrics.analyzer

class BasicMetricsAnalyzer : MetricsAnalyzer<BasicMetrics> {
    override fun analyze(code: String, test: String): BasicMetrics {
        val totalTests = countAnnotations(test, "@Test")
        val totalAssertions = countAssertions(test)
        val averageAssertions = if (totalTests > 0) {
            totalAssertions.toDouble() / totalTests
        } else 0.0

        return BasicMetrics(
            totalTests = totalTests,
            totalAssertions = totalAssertions,
            averageAssertionsPerTest = averageAssertions
        )
    }

    private fun countAnnotations(test: String, annotation: String): Int {
        return test.split("\n")
            .count { it.trim().startsWith(annotation) }
    }

    private fun countAssertions(test: String): Int {
        val assertionTypes = setOf(
            "assertEquals",
            "assertTrue",
            "assertFalse",
            "assertNull",
            "assertNotNull",
            "assertThrows",
            "assertThat"
        )

        return test.split("\n")
            .count { line ->
                assertionTypes.any { assertion ->
                    line.trim().contains(assertion)
                }
            }
    }
}