package com.example.data.api

import com.example.data.model.WpPostDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface WordPressApiService {
    @GET("wp/v2/sites/guvengeziyor1.wordpress.com/posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("search") search: String? = null
    ): List<WpPostDto>

    companion object {
        private const val BASE_URL = "https://public-api.wordpress.com/"

        fun create(): WordPressApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(WordPressApiService::class.java)
        }
    }
}
