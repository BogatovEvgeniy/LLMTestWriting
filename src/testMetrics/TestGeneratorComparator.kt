package testMetrics

import generator.Generators
import java.io.File
import com.google.gson.Gson
import testMetrics.analyzer.BasicMetricsAnalyzer
import testMetrics.analyzer.CoverageAnalyzer
import testMetrics.analyzer.QualityAnalyzer
import testMetrics.analyzer.ReadabilityAnalyzer

class TestGeneratorComparator {
    private val analyzer = TestQualityAnalyzer(
        BasicMetricsAnalyzer(),
        CoverageAnalyzer(),
        QualityAnalyzer(),
        ReadabilityAnalyzer(),
    )

    private val gson = Gson()

    fun compareGenerators(
        originalCode: String,
        generatedTests: Map<Generators, String>
    ): List<ComparisonResult> {
        return generatedTests.map { (generator, test) ->
            ComparisonResult(
                generator = generator,
                analysis = analyzer.analyzeTest(originalCode, test),
                timestamp = System.currentTimeMillis()
            )
        }.sortedByDescending { it.analysis.score }
    }

    fun saveResults(results: List<ComparisonResult>, outputPath: String) {
        val timestamp = System.currentTimeMillis()
        val reportFile = File(outputPath, "comparison_report_$timestamp.json")
        reportFile.writeText(gson.toJson(results))

        // Створення текстового звіту
        val reportText = generateTextReport(results)
        File(outputPath, "comparison_report_$timestamp.txt")
            .writeText(reportText)
    }

    private fun generateTextReport(results: List<ComparisonResult>): String {
        val sb = StringBuilder()
        sb.appendLine("Test Generator Comparison Report")
        sb.appendLine("================================")

        results.forEachIndexed { index, result ->
            sb.appendLine("\n${index + 1}. ${result.generator}")
            sb.appendLine("Score: ${result.analysis.score}")
            sb.appendLine("Basic Metrics:")
            sb.appendLine("  - Total Tests: ${result.analysis.basicMetrics.totalTests}")
            sb.appendLine("  - Total Assertions: ${result.analysis.basicMetrics.totalAssertions}")
            sb.appendLine("  - Avg Assertions per Test: ${result.analysis.basicMetrics.averageAssertionsPerTest}")

            sb.appendLine("Coverage:")
            sb.appendLine("  - Methods Covered: ${result.analysis.coverageMetrics.methodsCovered}")
            sb.appendLine("  - Edge Cases: ${result.analysis.coverageMetrics.edgeCasesCovered.count { it.covered }}/${result.analysis.coverageMetrics.edgeCasesCovered.size}")

            sb.appendLine("Quality:")
            sb.appendLine("  - Descriptive Names: ${result.analysis.qualityMetrics.hasDescriptiveNames}")
            sb.appendLine("  - Assertion Variety: ${result.analysis.qualityMetrics.usesAssertionVariety}")

            sb.appendLine("Readability:")
            sb.appendLine("  - Uses Backticks: ${result.analysis.readabilityMetrics.usesBackticks}")
            sb.appendLine("  - Has Comments: ${result.analysis.readabilityMetrics.hasComments}")
            sb.appendLine("  - Average Test Length: ${result.analysis.readabilityMetrics.averageTestLength}")
        }

        return sb.toString()
    }
}