package generator

import com.epam.oko.imageDescription.data.model.dial.ChatRequest
import com.epam.oko.imageDescription.data.model.dial.Message
import com.epam.oko.imageDescription.data.model.dial.ResponseModel
import generator.GptTestGenerator.API_KEY
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.time.Duration


object GptTestGenerator : TestGenerator {

    const val API_KEY =
        ""
    const val HOST = "https://api.openai.com/v1/"
    const val GPT_MODEL = ""

    var service: GPTCompletionService


    init {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder()
                    .header("Api-Key", API_KEY)
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(Duration.ofSeconds(20))
            .callTimeout(Duration.ofSeconds(20))
            .readTimeout(Duration.ofSeconds(20))
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(HOST)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build()

        service = retrofit.create(GPTCompletionService::class.java)
    }

    override fun generateTestsFor(llmPrompt: String, code: String): String {
        val responseBody = service.chatCompletions(
            ChatRequest(model = GPT_MODEL, store = true, messages = listOf(Message(content = "$llmPrompt for $code")))
        ).execute().body()
        return responseBody?.choices?.firstOrNull()?.message?.content ?: "An empty value has been returned"
    }
}

interface GPTCompletionService {

    @Headers("Content-Type: application/json", "Authorization: $API_KEY")
    @POST("chat/completions")
    fun chatCompletions(
        @Body chatRequest: ChatRequest,
    ): Call<ResponseModel>
}