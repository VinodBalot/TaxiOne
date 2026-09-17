package com.example.taxione.data.mock

import com.example.taxione.data.dto.BookRequestDto
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import javax.inject.Inject

class MockInterceptor @Inject constructor(
    private val server: FakeBooksServer,
) : Interceptor {

    private val json = Json { ignoreUnknownKeys = true }
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (!path.contains("books")) return chain.proceed(request)

        val body = when (request.method) {
            "POST" -> {
                val requestBody = request.body?.let { body ->
                    val buffer = Buffer()
                    body.writeTo(buffer)
                    buffer.readUtf8()
                } ?: "{}"
                val dto = json.decodeFromString<BookRequestDto>(requestBody)
                server.createBooking(dto)
            }
            "GET" -> {
                val year = request.url.queryParameter("year")?.toIntOrNull() ?: 0
                val month = request.url.queryParameter("month")?.toIntOrNull() ?: 0
                server.getBookings(year, month)
            }
            else -> "{}"
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(body.toResponseBody(mediaType))
            .build()
    }
}
