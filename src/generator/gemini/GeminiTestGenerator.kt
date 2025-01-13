package generator.gemini

import Secrets
import generator.RestClientFactory
import generator.TestGenerator
import generator.gemini.model.ContentPart
import generator.gemini.model.RequestData
import generator.gemini.model.ResponseData
import generator.gemini.model.TextPart
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

object GeminiTestGenerator : TestGenerator {
    val service: GenerateContentService

    init {
        val retrofit = RestClientFactory.create(Secrets.GeminiSecret)
        service = retrofit.create(GenerateContentService::class.java)
    }

    override fun generateTestsFor(llmPrompt: String, code: String): String {
        val responseBody = service.generateContent(
            RequestData(contents = listOf(ContentPart(parts = listOf(TextPart("$llmPrompt for $code"))))),
            Secrets.GeminiSecret.apiKey
        ).execute().body()
        return responseBody?.candidates?.firstOrNull()?.content?.toString() ?: "An empty value has been returned"
    }

    interface GenerateContentService {
        @Headers("Content-Type: application/json")
        @POST("models/gemini-1.5-flash:generateContent")
        fun generateContent(
            @Body requestData: RequestData,
            @Query("key") apiKey: String
        ): Call<ResponseData>
    }
}
