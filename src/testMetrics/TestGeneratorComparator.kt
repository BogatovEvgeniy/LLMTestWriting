package testMetrics

import generator.Generators
import java.io.File
import com.google.gson.Gson
import testMetrics.model.GenerationResult

class TestGeneratorComparator(
    private val analyzer: TestQualityAnalyzer = TestQualityAnalyzer()
) {
    private val gson = Gson()

    fun compareGenerators(
        originalCode: String,
        generatedTests: Map<Generators, GenerationResult>
    ): List<ComparisonResult> {
        return generatedTests.map { (generator, result) ->
            val startAnalysis = System.currentTimeMillis()
            val analysis = analyzer.analyzeTest(originalCode, result.tests)
            val analysisTime = System.currentTimeMillis() - startAnalysis

            val updatedTimingMetrics = result.timingMetrics.copy(
                testAnalysisTimeMs = analysisTime
            )

            ComparisonResult(
                generator = generator,
                analysis = analysis,
                timestamp = System.currentTimeMillis(),
                timingMetrics = updatedTimingMetrics
            )
        }.sortedByDescending { it.analysis.score }
    }

    fun saveResults(results: List<ComparisonResult>, outputPath: String) {
        val timestamp = System.currentTimeMillis()

        // Save JSON report
        val reportFile = File(outputPath, "comparison_report_$timestamp.json")
        reportFile.writeText(gson.toJson(results))

        // Save text report
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

            // Basic Metrics
            sb.appendLine("Basic Metrics:")
            sb.appendLine("  - Total Tests: ${result.analysis.basicMetrics.totalTests}")
            sb.appendLine("  - Total Assertions: ${result.analysis.basicMetrics.totalAssertions}")
            sb.appendLine("  - Avg Assertions per Test: ${result.analysis.basicMetrics.averageAssertionsPerTest}")

            // Coverage
            sb.appendLine("Coverage:")
            sb.appendLine("  - Methods Covered: ${result.analysis.coverageMetrics.methodsCovered}")
            sb.appendLine("  - Edge Cases: ${result.analysis.coverageMetrics.edgeCasesCovered.count { it.covered }}/${result.analysis.coverageMetrics.edgeCasesCovered.size}")

            // Quality
            sb.appendLine("Quality:")
            sb.appendLine("  - Descriptive Names: ${result.analysis.qualityMetrics.hasDescriptiveNames}")
            sb.appendLine("  - Assertion Variety: ${result.analysis.qualityMetrics.usesAssertionVariety}")

            // Readability
            sb.appendLine("Readability:")
            sb.appendLine("  - Uses Backticks: ${result.analysis.readabilityMetrics.usesBackticks}")
            sb.appendLine("  - Has Comments: ${result.analysis.readabilityMetrics.hasComments}")
            sb.appendLine("  - Average Test Length: ${result.analysis.readabilityMetrics.averageTestLength}")

            // Timing Metrics
            sb.appendLine("Timing:")
            with(result.timingMetrics) {
                sb.appendLine("  - Total Processing Time: ${totalProcessingTimeMs}ms")
                sb.appendLine("  - Total Generation Time: ${totalGenerationTimeMs}ms")
                sb.appendLine("  - API Call Time: ${apiCallTimeMs}ms")
                sb.appendLine("  - Average File Generation Time: ${averageFileGenerationTimeMs}ms")
                sb.appendLine("  - Parsing Time: ${parsingTimeMs}ms")
                sb.appendLine("  - Analysis Time: ${testAnalysisTimeMs}ms")
            }
        }

        return sb.toString() // Moved outside the forEachIndexed
    }
}