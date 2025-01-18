import generator.Generators
import generator.TestGeneratorFactory
import prompt.SimplePrompt
import testMetrics.model.GenerationResult
import testMetrics.timing.TimingCollector
import java.io.File
import com.google.gson.Gson

fun main() {
    val sysDir = System.getProperty("user.dir")
    val promptGenerator = SimplePrompt()
    val gson = Gson()

    // List of generators to use
    val generators = listOf(
        Generators.GPT,
        Generators.GEMINI,
        Generators.CODELLAMA
    )

    // Create session folders if they don't exist
    val sessionNumber = getNextSessionNumber(sysDir)
    println("Starting Session $sessionNumber")

    // Generate and save tests with timing info
    generators.forEach { generator ->
        try {
            generateTestsForLLM(
                generator = generator,
                promptGenerator = promptGenerator,
                sysDir = sysDir,
                sessionNumber = sessionNumber,
                gson = gson
            )
        } catch (e: Exception) {
            println("Error processing ${generator.name}: ${e.message}")
        }
    }

    println("Session $sessionNumber completed")
}

private fun generateTestsForLLM(
    generator: Generators,
    promptGenerator: SimplePrompt,
    sysDir: String,
    sessionNumber: Int,
    gson: Gson
) {
    println("\nGenerating tests using ${generator.name}")
    val timingCollector = TimingCollector()  // Create one collector for the entire generation
    timingCollector.start()

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
            val fileStartTime = System.currentTimeMillis()

            // Measure prompt generation
            timingCollector.markTime("prompt_start")
            val code = file.readText()
            val llmPrompt = promptGenerator.generatePrompt()
            timingCollector.markTime("prompt_end")

            // Measure API call
            timingCollector.markTime("api_start")
            val generatedTests = testGenerator.generateTestsFor(llmPrompt, code)
            timingCollector.markTime("api_end")

            // Calculate and record file generation time
            val fileGenerationTime = System.currentTimeMillis() - fileStartTime
            timingCollector.recordFileGeneration(file.name, fileGenerationTime)

            // Save test content
            File(sessionFolder, file.name).writeText(generatedTests)

            println("Tests generated for: ${file.name}")
            println("Generation time: ${fileGenerationTime}ms")
        } catch (e: Exception) {
            println("Error processing ${file.name}: ${e.message}")
        }
    }

    // Save timing metrics for the entire session
    val timingMetrics = timingCollector.getTimingMetrics()
    File(sessionFolder, "timing.json").writeText(gson.toJson(timingMetrics))
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