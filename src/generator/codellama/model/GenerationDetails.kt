package generator.codellama.model

data class GenerationDetails(
    val finishReason: String,
    val generatedTokens: Int,
    val seedValue: Long?
)