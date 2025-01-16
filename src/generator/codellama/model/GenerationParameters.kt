package generator.codellama.model

data class GenerationParameters(
    val maxNewTokens: Int,
    val temperature: Double,
    val topP: Double,
    val repetitionPenalty: Double,
    val returnFullText: Boolean = false,
    val numReturnSequences: Int = 1
)
