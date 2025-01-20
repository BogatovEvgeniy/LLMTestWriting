package coordinator

import generator.Generators
import generator.TestGeneratorFactory
import prompt.SimplePrompt
import testMetrics.TestGeneratorComparator
import testMetrics.model.GenerationResult
import testMetrics.timing.TimingCollector
import java.io.File
import com.google.gson.Gson
import testMetrics.timing.TimingMetrics

object MainProcess {
    private val gson = Gson()

    fun run(config: TestGenerationConfig): String {
        val results = StringBuilder()

        try {
            // Generate Tests
            results.append("Starting test generation...\n")
            generateTests(config)
            results.append("Test generation completed\n\n")

            // Analyze Tests
            results.append("Starting test analysis...\n")
            val analysisResults = analyzeTests()
            results.append(analysisResults)
            results.append("Analysis completed\n")
            results.append("Results of analysis...\n")
            runPythonProject()

            return results.toString()
        } catch (e: Exception) {
            return "Error during execution: ${e.message}"
        }
    }

    private fun generateTests(config: TestGenerationConfig) {
        val sysDir = System.getProperty("user.dir")
        val promptGenerator = SimplePrompt(config.prompt)

        // Get selected generators
        val generators = mutableListOf<Generators>().apply {
            if (config.useGPT) add(Generators.GPT)
            if (config.useGemini) add(Generators.GEMINI)
            if (config.useCodeLlama) add(Generators.CODELLAMA)
        }

        // Create session
        val sessionNumber = getNextSessionNumber(sysDir)
        println("Starting Session $sessionNumber")

        // Generate tests for each selected LLM
        generators.forEach { generator ->
            try {
                generateTestsForLLM(
                    generator = generator,
                    promptGenerator = promptGenerator,
                    sysDir = sysDir,
                    sessionNumber = sessionNumber
                )
            } catch (e: Exception) {
                println("Error processing ${generator.name}: ${e.message}")
            }
        }
    }

    private fun generateTestsForLLM(
        generator: Generators,
        promptGenerator: SimplePrompt,
        sysDir: String,
        sessionNumber: Int
    ) {
        println("\nGenerating tests using ${generator.name}")

        val resultPath = when(generator) {
            Generators.GPT -> "result/gpt"
            Generators.GEMINI -> "result/gemini"
            Generators.CODELLAMA -> "result/codellama"
            else -> "result/other"
        }

        val testGenerator = TestGeneratorFactory.create(generator)
        val sessionFolder = File(sysDir, "$resultPath/session$sessionNumber")
        sessionFolder.mkdirs()

        // Process each file in testData
        val codeDir = File(sysDir, "testData")
        codeDir.listFiles()?.forEach { file ->
            try {
                println("Processing: ${file.name}")
                val timingCollector = TimingCollector()
                timingCollector.start()

                // Measure prompt generation
                timingCollector.markTime("prompt_start")
                val code = file.readText()
                val llmPrompt = promptGenerator.generatePrompt()
                timingCollector.markTime("prompt_end")

                // Measure API call
                timingCollector.markTime("api_start")
                val generatedTests = testGenerator.generateTestsFor(llmPrompt, code)
                timingCollector.markTime("api_end")

                // Save test content
                File(sessionFolder, file.name).writeText(generatedTests)

                // Save timing metrics
                val timingMetrics = timingCollector.getTimingMetrics()
                File(sessionFolder, "timing.json").writeText(gson.toJson(timingMetrics))

                println("Tests generated for: ${file.name}")
                println("Saved to: ${sessionFolder.absolutePath}")
            } catch (e: Exception) {
                println("Error processing ${file.name}: ${e.message}")
            }
        }
    }

    private fun analyzeTests(): String {
        val sysDir = System.getProperty("user.dir")
        val testComparator = TestGeneratorComparator()
        val analysisResults = StringBuilder()

        // Read original files
        val codeDir = File(sysDir, "testData")
        codeDir.listFiles()?.forEach { originalFile ->
            analysisResults.append("\nAnalyzing tests for: ${originalFile.name}\n")
            val originalCode = originalFile.readText()

            // Collect and analyze tests
            val generatedTests = collectGeneratedTestsFromSessions(sysDir, originalFile.nameWithoutExtension)

            if (generatedTests.isNotEmpty()) {
                val results = testComparator.compareGenerators(originalCode, generatedTests)

                // Save results
                val analysisDir = File(sysDir, "analysis_results")
                analysisDir.mkdirs()
                testComparator.saveResults(results, analysisDir.absolutePath)

                // Add results to output
                results.forEach { result ->
                    analysisResults.append("\n${result.generator}:\n")
                    analysisResults.append("Score: ${result.analysis.score}\n")
                    analysisResults.append("Tests: ${result.analysis.basicMetrics.totalTests}\n")
                    analysisResults.append("Coverage: ${result.analysis.coverageMetrics.methodsCovered.size} methods\n")
                }
            } else {
                analysisResults.append("No generated tests found\n")
            }
        }

        return analysisResults.toString()
    }

    private fun collectGeneratedTestsFromSessions(
        sysDir: String,
        originalFileName: String
    ): Map<Generators, GenerationResult> {
        val generatedTests = mutableMapOf<Generators, GenerationResult>()

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

    private fun findLatestTestInSessions(
        sysDir: String,
        llmPath: String,
        originalFileName: String
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

            // Look for the timing file
            val timingFile = File(sessionDir, "timing.json")

            if (testFile != null) {
                val timingMetrics = if (timingFile.exists()) {
                    try {
                        gson.fromJson(timingFile.readText(), TimingMetrics::class.java)
                    } catch (e: Exception) {
                        println("Error reading timing metrics: ${e.message}")
                        null
                    }
                } else null

                return GenerationResult(
                    tests = testFile.readText(),
                    timingMetrics = timingMetrics ?: TimingMetrics(0, 0, 0, emptyMap(), 0.0, 0, 0)
                )
            }
        }

        return null
    }

    private fun getNextSessionNumber(sysDir: String): Int {
        val resultDirs = listOf("result/gpt", "result/gemini", "result/codellama")
        var maxSession = 0

        resultDirs.forEach { dirPath ->
            val dir = File(sysDir, dirPath)
            if (dir.exists()) {
                dir.listFiles { file ->
                    file.isDirectory && file.name.startsWith("session")
                }?.forEach { sessionDir ->
                    val sessionNum = sessionDir.name.removePrefix("session").toIntOrNull() ?: 0
                    if (sessionNum > maxSession) {
                        maxSession = sessionNum
                    }
                }
            }
        }

        return maxSession + 1
    }

    private fun runPythonProject() {
        val pythonPath = "python"  // or "python3" depending on your system
        val scriptPath = "LLMComparisonAnalyzer.py"

        val process = ProcessBuilder(pythonPath, scriptPath)
            .redirectErrorStream(true)
            .start()

        // Read output
        val output = process.inputStream.bufferedReader().use { it.readText() }

        // Wait for completion
        val exitCode = process.waitFor()
        if (exitCode != 0) {
            throw RuntimeException("Python script failed with exit code $exitCode: $output")
        }
    }
}