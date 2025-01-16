package testMetrics.collector

import testMetrics.ComparisonResult
import testMetrics.TestQualityAnalyzer
import testMetrics.session.TestSession

class TestResultsCollector(
    private val analyzer: TestQualityAnalyzer,
    private val sessionManager: TestSession
) {
    fun collectResults(
        originalCode: String,
        sessionId: Int
    ): List<ComparisonResult> {
        val session = sessionManager.getSession(sessionId)
        val tests = session.loadResults()
        return analyzer.analyzeTests(originalCode, tests)
    }
}
}