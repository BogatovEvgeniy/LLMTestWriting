package testMetrics.collector

import testMetrics.ComparisonResult
import testMetrics.TestQualityAnalyzer
import testMetrics.session.TestSessionImpl

class TestResultsCollector(
    private val analyzer: TestQualityAnalyzer,
    private val sessionManager: TestSessionImpl
) {
    fun collectResults(
        originalCode: String,
        fileName: String,
    ): List<ComparisonResult> {
        val tests = sessionManager.loadResults(fileName)
        val qualityResults = mutableListOf<ComparisonResult>()
        tests.entries.forEach {
            qualityResults.add(
                ComparisonResult(
                    generator = it.key,
                    analysis = analyzer.analyzeTest(originalCode, it.value)
                )
            )
        }
        return qualityResults
    }
}
