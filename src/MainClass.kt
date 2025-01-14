import generator.Generators
import generator.TestGenerator
import generator.TestGeneratorFactory
import prompt.SimplePrompt
import java.io.File

const val CODE_DIR_NAME = "testData"
const val RESULT_DIR_NAME = "result"

fun main(args: Array<String>) {
    val sysDir = System.getProperty("user.dir")
    val copilotPath = "result/copilot"
    val geminiPath = "result/gemini"
    val gptPath = "result/gpt"
    val codeLlamaPath = "result/codellama"  // Added CodeLlama path
    val promptGenerator = SimplePrompt()

    // You can change this to use different generators
    val testGenerator = TestGeneratorFactory.create(Generators.CODELLAMA)

    // Updated list to include CodeLlama
    val llmList = listOf(gptPath, geminiPath, codeLlamaPath)

    llmList.forEach { llmPath ->
        try {
            println("Launch logic for llm path: $llmPath")
            if (validateDirectoryStructure(
                    codeDirectoryPath = sysDir + File.separator + CODE_DIR_NAME,
                    llmPath = sysDir + File.separator + llmPath
                )
            ) {
                processCodebase(sysDir, llmPath, promptGenerator, testGenerator)
            }
        } catch (e: Exception) {
            println("Error processing $llmPath: ${e.message}")
        }
    }
}

private fun processCodebase(
    sysDir: String,
    llmPath: String,
    promptGenerator: SimplePrompt,
    testGenerator: TestGenerator
) {
    val codebasePath = sysDir + File.separator + CODE_DIR_NAME
    val codebaseFiles = getPathContent(codebasePath)

    codebaseFiles?.forEach { file ->
        try {
            println("Processing code of file: ${file.name}")
            val codebase = readFileCodebase(codebasePath, file.name)

            println("Code read from file: ${codebase.take(30)}...")
            val llmPrompt = promptGenerator.generatePrompt()

            println("Use prompt: ${llmPrompt.take(30)}...")
            val result = testGenerator.generateTestsFor(llmPrompt, codebase)

            println("Result is received: ${result.take(30)}...")
            savePromptResult(sysDir + File.separator + llmPath, file.name, result)
        } catch (e: Exception) {
            println("Error processing file ${file.name}: ${e.message}")
        }
    }
}

@Throws(IllegalArgumentException::class)
private fun validateDirectoryStructure(codeDirectoryPath: String, llmPath: String): Boolean {
    val llmDirectory = File(llmPath)
    val codeDirectory = File(codeDirectoryPath)

    return try {
        when {
            !llmDirectory.exists() || !llmDirectory.isDirectory ->
                throw IllegalArgumentException("The LLM path is not a valid directory: $llmPath")
            !codeDirectory.exists() || !codeDirectory.isDirectory ->
                throw IllegalArgumentException("The code directory path is not valid: $codeDirectoryPath")
            codeDirectory.listFiles().isNullOrEmpty() ->
                throw IllegalArgumentException("The code directory is empty: ${codeDirectory.absolutePath}")
            else -> true
        }
    } catch (e: Exception) {
        println("Directory structure validation failed: ${e.message}")
        false
    }
}

private fun getPathContent(codePath: String): Array<out File>? =
    File(codePath).listFiles()

private fun readFileCodebase(codePath: String, fileName: String): String {
    val codebaseFile = File(codePath + File.separator + fileName)
    return when {
        codebaseFile.exists() && codebaseFile.isFile -> codebaseFile.readText()
        else -> throw IllegalArgumentException("Invalid file path: ${codebaseFile.absolutePath}")
    }
}

private fun savePromptResult(folderPath: String, fileName: String, testCodebase: String) {
    val timeStamp = System.currentTimeMillis()
    val resultsFolderPath = folderPath + File.separator + RESULT_DIR_NAME + "_" + timeStamp
    val resultsFolder = File(resultsFolderPath)
    val resultFile = File(resultsFolderPath + File.separator + fileName)

    println("Saving results to $resultsFolderPath")

    try {
        resultsFolder.mkdirs()
        resultFile.createNewFile()
        resultFile.writeText(testCodebase)
    } catch (e: Exception) {
        println("Error saving results: ${e.message}")
    }
}