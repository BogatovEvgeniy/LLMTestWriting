package generator.gpt

import Secrets
import com.epam.oko.imageDescription.data.model.dial.ChatRequest
import com.epam.oko.imageDescription.data.model.dial.Message
import com.epam.oko.imageDescription.data.model.dial.ResponseModel
import generator.RestClientFactory
import generator.TestGenerator
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


object GptTestGenerator : TestGenerator {

    val GPT_MODEL = "gpt-4o-mini"
    var service: GPTCompletionService

    init {
        val retrofit = RestClientFactory.create(Secrets.gptHost)
        service = retrofit.create(GPTCompletionService::class.java)
    }

    override fun generateTestsFor(llmPrompt: String, code: String): String {
        val response = service.chatCompletions(
            ChatRequest(model = GPT_MODEL, store = true, messages = listOf(Message(content = "$llmPrompt for $code")))
        ).execute()
        return response.body()?.choices?.firstOrNull()?.message?.content ?: "An empty value has been returned"
    }

    interface GPTCompletionService {


        @Headers(
            "Content-Type: application/json",
            "Authorization:${Secrets.gptApiKey}"
        )
        @POST("chat/completions")
        fun chatCompletions(
            @Body chatRequest: ChatRequest,
        ): Call<ResponseModel>
    }
}
