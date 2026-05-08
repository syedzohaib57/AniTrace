package com.example.anitrace.data


import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.anitrace.domain.TraceResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiService {

    private val lenientJson = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(lenientJson)
        }
    }

    suspend fun searchAnime(imageUrl: String): TraceResponse {
        return try {
            val response = client.get("$baseUrl/search") {
                parameter("url", extractRealImageUrl(imageUrl))
                parameter("anilistInfo", true)
                parameter("cutBorders", true)
            }

            val rawJson = response.bodyAsText()
            Log.d("ApiService", "Raw response: $rawJson")

            lenientJson.decodeFromString<TraceResponse>(rawJson)

        } catch (e: Exception) {
            Log.e("ApiService", "searchAnime failed", e)
            TraceResponse(
                result = emptyList(),
                error = e.message
            )
        }
    }
    // ApiService.kt
    suspend fun uploadAndSearch(context: Context, uri: Uri): TraceResponse {
        return try {
            val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                ?: return TraceResponse(result = emptyList(), error = "Cannot read image")

            val response = client.post("$baseUrl/search?anilistInfo=true&cutBorders=true") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("image", bytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=image.jpg")
                            })
                        }
                    )
                )
            }

            val rawJson = response.bodyAsText()
            lenientJson.decodeFromString<TraceResponse>(rawJson)

        } catch (e: Exception) {
            Log.e("ApiService", "uploadAndSearch failed", e)
            TraceResponse(result = emptyList(), error = e.message)
        }
    }
}