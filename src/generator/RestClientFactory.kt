package generator

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

object RestClientFactory {
    fun create(host: String): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(120))
            .callTimeout(Duration.ofSeconds(120))
            .readTimeout(Duration.ofSeconds(120))
            .build()
        return Retrofit.Builder()
            .baseUrl(host)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build()
    }
}