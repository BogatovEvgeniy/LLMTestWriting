import generator.Generators
import generator.TestGeneratorFactory
import prompt.SimplePrompt
import testMetrics.session.TestSessionManager
import java.io.File

fun main() {
    val sysDir = System.getProperty("user.dir")
    val promptGenerator = SimplePrompt()
    val sessionManager = TestSessionManager(sysDir)

    // List of generators to use
    val generators = listOf(
        Generators.GPT,
        Generators.GEMINI,
        //Generators.CODELLAMA
    )

    // Create new session
    val session = sessionManager.createSession(generators)
    println("Starting Session ${session.sessionId}")

    // Generate and save tests
    val results = mutableMapOf<Generators, String>()

    // Process each generator
    generators.forEach { generator ->
        try {
            processGenerator(
                generator = generator,
                promptGenerator = promptGenerator,
                sysDir = sysDir,
                results = results
            )
        } catch (e: Exception) {
            println("Error processing ${generator.name}: ${e.message}")
        }
    }

    // Save session results
    session.saveResults(results)
    println("Session ${session.sessionId} completed")
}

private fun processGenerator(
    generator: Generators,
    promptGenerator: SimplePrompt,
    sysDir: String,
    results: MutableMap<Generators, String>
) {
    println("\nGenerating tests using ${generator.name}")

    val testGenerator = TestGeneratorFactory.create(generator)

    // Process each file in testData
    val codeDir = File(sysDir, "testData")
    val testsForGenerator = StringBuilder()

    codeDir.listFiles()?.forEach { file ->
        try {
            println("Processing: ${file.name}")
            val code = file.readText()
            val llmPrompt = promptGenerator.generatePrompt()

            // Generate tests
            val generatedTests = testGenerator.generateTestsFor(llmPrompt, code)
            testsForGenerator.append(generatedTests).append("\n\n")

            println("Tests generated for: ${file.name}")
        } catch (e: Exception) {
            println("Error processing ${file.name}: ${e.message}")
        }
    }

    // Store all tests for this generator
    results[generator] = testsForGenerator.toString()
}