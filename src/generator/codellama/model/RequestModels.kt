package generator.codellama.model

data class LlamaCompletionRequest(
    val messages: List<Message>,
    val model: String = "llama3.1-70b",
    val parameters: GenerationParameters,
//    val functions: List<Function> = emptyList(),
//    val stream: Boolean = false,
//    val function_call: String = ""
)

data class Message(
    val role: String,
    val content: String
)

data class Function(
    val name: String,
    val description: String,
    val parameters: Parameters,
    val required: List<String>
)

data class Parameters(
    val type: String,
    val properties: Properties
)

data class Properties(
    val location: Location,
    val days: Days,
    val unit: Unit
)

data class Location(
    val type: String,
    val description: String
)

data class Days(
    val type: String,
    val description: String
)

data class Unit(
    val type: String,
    val enum: List<String>
)