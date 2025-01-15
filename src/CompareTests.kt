import generator.Generators
import testMetrics.TestGeneratorComparator
import java.io.File
import testMetrics.*

fun main() {
    val sysDir = System.getProperty("user.dir")
    val testComparator = TestGeneratorComparator()

    // Read original files
    val codeDir = File(sysDir, "testData")
    codeDir.listFiles()?.forEach { originalFile ->
        println("\nAnalyzing tests for: ${originalFile.name}")
        val originalCode = originalFile.readText()

        // Collect generated tests across all sessions
        val generatedTests = collectGeneratedTestsFromSessions(sysDir, originalFile.nameWithoutExtension)

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

private fun collectGeneratedTestsFromSessions(sysDir: String, originalFileName: String): Map<Generators, String> {
    val generatedTests = mutableMapOf<Generators, String>()

    // Map of LLMs and their directories
    val llmPaths = mapOf(
        Generators.GPT to "result/gpt",
        Generators.GEMINI to "result/gemini",
        Generators.CODELLAMA to "result/codellama"
    )

    llmPaths.forEach { (generator, basePath) ->
        findLatestTestInSessions(sysDir, basePath, originalFileName)?.let {
            generatedTests[generator] = it
            println("Found ${generator.name} test in session")
        }
    }

    return generatedTests
}

private fun findLatestTestInSessions(sysDir: String, llmPath: String, originalFileName: String): String? {
    val baseDir = File(sysDir, llmPath)
    if (!baseDir.exists()) return null

    // Find the latest session directory
    val latestSessionDir = baseDir.listFiles { file ->
        file.isDirectory && file.name.startsWith("session")
    }?.maxByOrNull { it.lastModified() }

    latestSessionDir?.let { sessionDir ->
        // Find the file with the same name as originalFileName
        val targetFile = sessionDir.listFiles { file ->
            file.nameWithoutExtension == originalFileName
        }?.firstOrNull()

        if (targetFile != null) {
            return targetFile.readText()
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
    }
}