package generator.gemini.model

data class RequestData(
    val contents: List<ContentPart>
)

data class ContentPart(
    val parts: List<TextPart>
)

data class TextPart(
    val text: String
)