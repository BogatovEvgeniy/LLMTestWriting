package generator.codellama.model

data class CodeLlamaResponse(
    val generatedText: String,
    val tokenCount: Int? = null,
    val details: GenerationDetails? = null
)
