package generator.codellama

import Secrets
import generator.RestClientFactory
import generator.TestGenerator
import generator.codellama.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

object CodeLlamaTestGenerator : TestGenerator {
    private val service: CodeLlamaService

    init {
        val retrofit = RestClientFactory.create(Secrets.codellamaHost)
        service = retrofit.create(CodeLlamaService::class.java)
    }

    override fun generateTestsFor(llmPrompt: String, code: String): String {
        val response = service.generateTests(
            LlamaCompletionRequest(
               messages = listOf(Message(role = "user", content = buildPrompt(llmPrompt, code))),
//                parameters = GenerationParameters(
//                    maxNewTokens = 1000,
//                    temperature = 0.7,
//                    topP = 0.95,
//                    repetitionPenalty = 1.1
//                )
            )
        ).execute()

        return response.body()?.choices?. firstOrNull()?.toString()
            ?: "No test cases were generated"
    }

    private fun buildPrompt(llmPrompt: String, code: String): String {
        return """
        [INST] Write JUnit 5 unit tests for the following Kotlin code.
        Requirements: $llmPrompt

        Code to test:
        ```kotlin
        $code
        ```

        Generate comprehensive unit tests following best practices and make sure to:
        - Include edge cases
        - Test error conditions
        - Follow naming conventions
        - Add descriptive test names
        [/INST]
        """
    }

    interface CodeLlamaService {
        @Headers(
            "Content-Type: application/json",
            "Authorization: Bearer ${Secrets.codellamaApiKey}"
        )
        @POST("chat/completions")
        fun generateTests(@Body request: LlamaCompletionRequest): Call<LamaCompletionResponse>
    }
}