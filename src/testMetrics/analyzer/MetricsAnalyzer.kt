package testMetrics.analyzer

interface MetricsAnalyzer<T : Metrics> {
    fun analyze(code: String, test: String): T
    fun analyzeCoverage()
}