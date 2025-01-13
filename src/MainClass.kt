import generator.Generators
import generator.TestGeneratorFactory
import prompt.SimplePrompt
import java.io.File

const val CODE_DIR_NAME = "code"
const val RESULT_DIR_NAME = "result"

fun main(args: Array<String>) {
    val copilotPath = "/Users/Ievgen_Bogatov/IdeaProjects/LLMTestWriting/testData/copilot"
    val geminiPath = "/Users/Ievgen_Bogatov/IdeaProjects/LLMTestWriting/testData/gemini"
    val gptPath = "/Users/Ievgen_Bogatov/IdeaProjects/LLMTestWriting/testData/gpt"
    val promptGenerator = SimplePrompt()
    val testGenerator = TestGeneratorFactory.create(Generators.GPT)

    val llmList = listOf(gptPath, geminiPath, copilotPath)

    llmList.forEach { llmPath ->
        println("Launch logic for llm path: $llmPath")
        if (validateDirectoryStructure(llmPath)) {
            val codebasePath = llmPath + File.separator + CODE_DIR_NAME
            val codebaseFiles = getPathContent(codebasePath)
            codebaseFiles?.forEach { file ->
                println("Processing code of file: $file")
                val codebase = readFileCodebase(codebasePath, file.name)

                println("Code read form file: ${codebase.subSequence(0, 30)}")
                val llmPrompt = promptGenerator.generatePrompt()

                println("Use prompt : ${llmPrompt.subSequence(0, 30)}")
                val result = testGenerator.generateTestsFor(llmPrompt, codebase)

                println("Result is received: ${result.subSequence(0, 30)}")
                savePromptResult(llmPath, file.name, result)
            }
        }
    }
}

@Throws(IllegalArgumentException::class)
private fun validateDirectoryStructure(llmPath: String): Boolean {
    val file = File(llmPath)
    val codeDirectory = File(llmPath + File.separator + CODE_DIR_NAME)

    try {
        if (file.isDirectory.not() || !file.exists())
            throw IllegalArgumentException("The passed path is not a directory one or absent")
        if (codeDirectory.isDirectory.not() || !codeDirectory.exists())
            throw IllegalArgumentException("The passed code directory path is not a directory one or absent")
        if (codeDirectory.listFiles().isEmpty())
            throw IllegalArgumentException("The passed code dir ${codeDirectory.absolutePath} is empty")

    } catch (e: Exception) {
        print(e)
        println("Llm Path is invalid")
        return false
    }
    println("Llm Path is valid")
    return true
}

private fun getPathContent(codePath: String): Array<out File>? {
    val codeDirectory = File(codePath)
    return codeDirectory.listFiles()
}

private fun readFileCodebase(codePath: String, fileName: String): String {
    val codebaseFolder = File(codePath + File.separator + fileName)
    return if (codebaseFolder.exists() && codebaseFolder.isFile) {
        codebaseFolder.readText()
    } else {
        throw IllegalArgumentException("The passed path is not a file one or absent")
    }

}

private fun savePromptResult(folderPath: String, fileName: String, testCodebase: String) {
    val timeStamp = System.currentTimeMillis()
    val resultsFolder = File(folderPath + File.separator + RESULT_DIR_NAME)
    val resultsFolderForTheRunInstance = File(folderPath + File.separator + RESULT_DIR_NAME + "_" + timeStamp)
    val resultFile = File(resultsFolderForTheRunInstance.absolutePath + File.separator + fileName)
    println("Save results to ${resultsFolderForTheRunInstance.absolutePath}")
    val file = File(fileName)

    if (!file.exists()) resultsFolder.mkdirs()

    if (resultsFolderForTheRunInstance.exists()) {
        file.deleteRecursively()
        resultsFolderForTheRunInstance.createNewFile()
    }

    resultsFolderForTheRunInstance.mkdirs()
    resultFile.createNewFile()

    resultFile.writeText(testCodebase)
}