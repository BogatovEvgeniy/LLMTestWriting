package generator

interface TestGenerator {
    fun generateTestsFor(llmPrompt: String, code: String):String
}