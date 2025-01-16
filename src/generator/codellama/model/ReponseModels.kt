package generator.codellama.model

data class LamaCompletionResponse(
    val choices: List<Choice>
)

data class Choice(
    val index: Int,
    val message: ChoiceMessage,
    val finish_reason: String
)

data class ChoiceMessage(
    val role: String,
    val content: String?,
    val function_call: FunctionCall?
)

data class FunctionCall(
    val name: String,
    val arguments: FunctionArguments
)

data class FunctionArguments(
    val location: String,
    val days: Int,
    val unit: String
)