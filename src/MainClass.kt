import generator.Generators
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
    val promptGenerator = SimplePrompt()
    val testGenerator = TestGeneratorFactory.create(Generators.GPT)

    val llmList = listOf(gptPath, geminiPath)

    llmList.forEach { llmPath ->
        println("Launch logic for llm path: $llmPath")
        if (validateDirectoryStructure(
                codeDirectoryPath = sysDir + File.separator + CODE_DIR_NAME,
                llmPath = sysDir + File.separator + llmPath
            )
        ) {
            val codebasePath = sysDir + File.separator + CODE_DIR_NAME
            val codebaseFiles = getPathContent(codebasePath)
            codebaseFiles?.forEach { file ->
                println("Processing code of file: $file")
                val codebase = readFileCodebase(codebasePath, file.name)

                println("Code read form file: ${codebase.subSequence(0, 30)}")
                val llmPrompt = promptGenerator.generatePrompt()

                println("Use prompt : ${llmPrompt.subSequence(0, 30)}")
                val result = testGenerator.generateTestsFor(llmPrompt, codebase)

                println("Result is received: ${result.subSequence(0, 30)}")
                savePromptResult(sysDir + File.separator + llmPath, file.name, result)
            }
        }
    }
}

@Throws(IllegalArgumentException::class)
private fun validateDirectoryStructure(codeDirectoryPath: String, llmPath: String): Boolean {
    val llmDirectory = File(llmPath)
    val codeDirectory = File(codeDirectoryPath)

    try {
        if (llmDirectory.isDirectory.not() || !llmDirectory.exists())
            throw IllegalArgumentException("The passed path is not a directory one or absent")
        if (codeDirectory.isDirectory.not() || !codeDirectory.exists())
            throw IllegalArgumentException("The passed code directory path is not a directory one or absent")
        if (codeDirectory.listFiles().isEmpty())
            throw IllegalArgumentException("The passed code dir ${codeDirectory.absolutePath} is empty")

    } catch (e: Exception) {
        print(e)
        println("Directory structure is invalid")
        return false
    }
    println("Directory structure is valid")
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

    if (!resultsFolderForTheRunInstance.exists()) resultsFolder.mkdirs()

    if (resultsFolderForTheRunInstance.exists()) {
        file.deleteRecursively()
        resultsFolderForTheRunInstance.createNewFile()
    }

    resultsFolderForTheRunInstance.mkdirs()
    resultFile.createNewFile()

    resultFile.writeText(testCodebase)
}