package generator.codellama.model

data class CodeLlamaRequest(
    val inputs: String,
    val parameters: GenerationParameters
)