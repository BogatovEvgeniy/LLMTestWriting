package generator

class TestGeneratorFactory {
    companion object {
        fun create(generators: Generators): TestGenerator {
            return when (generators) {
                Generators.COPILOT -> CopilotTestGenerator
                Generators.GEMINI -> GeminiTestGenerator
                Generators.GPT -> GptTestGenerator
            }
        }
    }
}
