package com.example.taxione.di

import com.example.taxione.BuildConfig
import com.example.taxione.data.api.AqiApi
import com.example.taxione.data.api.BooksApi
import com.example.taxione.data.api.GeocodingApi
import com.example.taxione.data.mock.MockInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json { ignoreUnknownKeys = true }

    private const val BOOK_BASE_URL = "https://api.taxiOne.com/"
    private const val WAQI_BASE_URL = "https://api.waqi.info/"
    private const val CLOUD_BASE_URL = "https://api.bigdatacloud.net/"


    @Provides
    @Singleton
    fun provideBaseOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()

    @Provides
    @Singleton
    @BookApi
    fun provideBooksOkHttpClient(
        base: OkHttpClient,
        mockInterceptor: MockInterceptor,
    ): OkHttpClient {
        val builder = base.newBuilder()
        if (BuildConfig.USE_MOCK_BOOKS) {
            builder.addInterceptor(mockInterceptor)
        }
        return builder.build()
    }

    private fun baseRetrofitBuilder(): Retrofit.Builder =
        Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))

    @Provides
    @Singleton
    @AQIApi
    fun provideAqicnRetrofit(base: OkHttpClient): Retrofit =
        baseRetrofitBuilder()
            .baseUrl(WAQI_BASE_URL)
            .client(base)
            .build()

    @Provides
    @Singleton
    @BigDataCloudApi
    fun provideBigDataCloudRetrofit(base: OkHttpClient): Retrofit =
        baseRetrofitBuilder()
            .baseUrl(CLOUD_BASE_URL)
            .client(base)
            .build()

    @Provides
    @Singleton
    @BookApi
    fun provideBooksRetrofit(@BookApi booksClient: OkHttpClient): Retrofit =
        baseRetrofitBuilder()
            .baseUrl(BOOK_BASE_URL)
            .client(booksClient)
            .build()

    @Provides
    @Singleton
    @AQIApi
    fun provideAqiApi(@AQIApi retrofit: Retrofit): AqiApi =
        retrofit.create(AqiApi::class.java)

    @Provides
    @Singleton
    @BigDataCloudApi
    fun provideGeocodingApi(@BigDataCloudApi retrofit: Retrofit): GeocodingApi =
        retrofit.create(GeocodingApi::class.java)

    @Provides
    @Singleton
    @BookApi
    fun provideBooksApi(@BookApi retrofit: Retrofit): BooksApi =
        retrofit.create(BooksApi::class.java)
}
