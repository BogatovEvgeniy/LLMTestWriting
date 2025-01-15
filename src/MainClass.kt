import generator.Generators
import generator.TestGeneratorFactory
import prompt.SimplePrompt
import java.io.File

fun main() {
    val sysDir = System.getProperty("user.dir")
    val promptGenerator = SimplePrompt()

    // Create session folder
    val sessionNumber = getNextSessionNumber(sysDir)
    println("Starting Session $sessionNumber")

    // Process each LLM
    listOf(
        Generators.GPT,
        Generators.GEMINI,
        //Generators.CODELLAMA
    ).forEach { generator ->
        generateTestsForLLM(sysDir, sessionNumber, generator, promptGenerator)
    }
}

private fun generateTestsForLLM(
    sysDir: String,
    sessionNumber: Int,
    generator: Generators,
    promptGenerator: SimplePrompt
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
            val code = file.readText()
            val llmPrompt = promptGenerator.generatePrompt()

            // Generate and save tests
            val generatedTests = testGenerator.generateTestsFor(llmPrompt, code)
            File(sessionFolder, file.name).writeText(generatedTests)

            println("Tests generated for: ${file.name}")
            println("Saved to: ${sessionFolder.absolutePath}/${file.name}")
        } catch (e: Exception) {
            println("Error processing ${file.name}: ${e.message}")
        }
    }
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