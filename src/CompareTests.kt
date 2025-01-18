import generator.Generators
import testMetrics.TestGeneratorComparator
import java.io.File
import testMetrics.*
import testMetrics.model.GenerationResult
import testMetrics.timing.TimingMetrics
import com.google.gson.Gson

fun main() {
    val sysDir = System.getProperty("user.dir")
    val testComparator = TestGeneratorComparator()
    val gson = Gson()

    // Read original files
    val codeDir = File(sysDir, "testData")
    codeDir.listFiles()?.forEach { originalFile ->
        println("\nAnalyzing tests for: ${originalFile.name}")
        val originalCode = originalFile.readText()

        // Collect generated tests across all sessions
        val generatedTests = collectGeneratedTestsFromSessions(sysDir, originalFile.nameWithoutExtension, gson)

        if (generatedTests.isNotEmpty()) {
            // Analyze and compare tests
            val results = testComparator.compareGenerators(originalCode, generatedTests)

            // Create directory for results
            val analysisDir = File(sysDir, "analysis_results/${originalFile.nameWithoutExtension}")
            analysisDir.mkdirs()

            // Save results
            testComparator.saveResults(results, analysisDir.absolutePath)

            // Print summary
            printResults(originalFile.name, results)
        } else {
            println("No generated tests found for ${originalFile.name}")
        }
    }
}

private fun collectGeneratedTestsFromSessions(
    sysDir: String,
    originalFileName: String,
    gson: Gson
): Map<Generators, GenerationResult> {
    val generatedTests = mutableMapOf<Generators, GenerationResult>()

    // Map of LLMs and their directories
    val llmPaths = mapOf(
        Generators.GPT to "result/gpt",
        Generators.GEMINI to "result/gemini",
        Generators.CODELLAMA to "result/codellama"
    )

    llmPaths.forEach { (generator, basePath) ->
        findLatestTestInSessions(sysDir, basePath, originalFileName, gson)?.let {
            generatedTests[generator] = it
            println("Found ${generator.name} test in session")
        }
    }

    return generatedTests
}

private fun findLatestTestInSessions(
    sysDir: String,
    llmPath: String,
    originalFileName: String,
    gson: Gson
): GenerationResult? {
    val baseDir = File(sysDir, llmPath)
    if (!baseDir.exists()) return null

    // Find the latest session directory
    val latestSessionDir = baseDir.listFiles { file ->
        file.isDirectory && file.name.startsWith("session")
    }?.maxByOrNull { it.lastModified() }

    latestSessionDir?.let { sessionDir ->
        // Find test file
        val testFile = sessionDir.listFiles { file ->
            file.nameWithoutExtension == originalFileName
        }?.firstOrNull()

        // Look for the general timing file for the session
        val timingFile = File(sessionDir, "timing.json")

        if (testFile != null) {
            val timingMetrics = if (timingFile.exists()) {
                try {
                    println("Reading timing metrics from ${timingFile.absolutePath}")
                    gson.fromJson(timingFile.readText(), TimingMetrics::class.java)
                } catch (e: Exception) {
                    println("Error reading timing metrics: ${e.message}")
                    TimingMetrics(0, 0, 0, emptyMap(), 0.0, 0, 0)
                }
            } else {
                println("Timing file not found in ${timingFile.absolutePath}")
                TimingMetrics(0, 0, 0, emptyMap(), 0.0, 0, 0)
            }

            return GenerationResult(
                tests = testFile.readText(),
                timingMetrics = timingMetrics
            )
        }
    }

    return null
}

private fun printResults(fileName: String, results: List<ComparisonResult>) {
    println("\nTest Analysis Summary for $fileName:")
    results.forEach { result ->
        println("\n${result.generator}:")
        println("Overall Score: ${result.analysis.score}")
        println("Total Tests: ${result.analysis.basicMetrics.totalTests}")
        println("Assertions per Test: ${result.analysis.basicMetrics.averageAssertionsPerTest}")
        println("Methods Covered: ${result.analysis.coverageMetrics.methodsCovered.size}")
        println("Edge Cases: ${result.analysis.coverageMetrics.edgeCasesCovered.count { it.covered }}")

        // Timing Metrics
        with(result.timingMetrics) {
            println("Generation Time: ${totalGenerationTimeMs}ms")
            println("API Call Time: ${apiCallTimeMs}ms")
            println("Average Generation Time per File: ${averageFileGenerationTimeMs}ms")
            println("Analysis Time: ${testAnalysisTimeMs}ms")
            println("Total Processing Time: ${totalProcessingTimeMs}ms")
        }
    }
}