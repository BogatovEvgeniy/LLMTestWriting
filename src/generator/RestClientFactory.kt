package generator

import Secrets
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration

object RestClientFactory {
    fun create(secret: Secrets): Retrofit {
        val client = OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(20))
            .callTimeout(Duration.ofSeconds(20))
            .readTimeout(Duration.ofSeconds(20))
            .build()
        return Retrofit.Builder()
            .baseUrl(secret.host)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client).build()
    }
}