package testMetrics.analyzer

class ReadabilityAnalyzer : MetricsAnalyzer<ReadabilityMetrics> { // Додаємо типізацію
    override fun analyze(code: String, test: String): ReadabilityMetrics {
        return ReadabilityMetrics(
            usesBackticks = test.contains("`"),
            hasComments = test.contains("//") || test.contains("/*"),
            averageTestLength = calculateAverageTestLength(test)
        )
    }

    private fun calculateAverageTestLength(test: String): Int {
        val testBlocks = test.split("@Test")
        return if (testBlocks.size <= 1) 0
        else {
            testBlocks.drop(1)
                .map { it.lines().count() }
                .average()
                .toInt()
        }
    }
}