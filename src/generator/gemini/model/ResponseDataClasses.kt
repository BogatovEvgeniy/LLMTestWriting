package generator.gemini.model

data class ResponseData(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata,
    val modelVersion: String
)

data class Candidate(
    val content: Content,
    val finishReason: String,
    val citationMetadata: CitationMetadata,
    val avgLogprobs: Double
)

data class Content(
    val parts: List<Part>,
    val role: String
)

data class Part(
    val text: String
)

data class CitationMetadata(
    val citationSources: List<CitationSource>
)

data class CitationSource(
    val startIndex: Int,
    val endIndex: Int,
    val uri: String
)

data class UsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int
)