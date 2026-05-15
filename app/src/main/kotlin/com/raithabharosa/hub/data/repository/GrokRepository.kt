package com.raithabharosa.hub.data.repository

import okhttp3.MediaType.Companion.toMediaType
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class GrokRepository(private val apiKey: String) {
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val groqUrl = "https://api.groq.com/openai/v1/chat/completions"
    private val defaultModel = "llama-3.1-8b-instant"

    // Simple send: posts prompt and returns raw response string
    fun send(prompt: String): Result<String> {
        return try {
            val payload = JSONObject()
                .put("model", defaultModel)
                .put("max_tokens", 512)
                .put(
                    "messages",
                    org.json.JSONArray()
                        .put(
                            JSONObject()
                                .put("role", "user")
                                .put("content", prompt)
                        )
                )
            val reqBody = payload.toString().toRequestBody(JSON)

            val request = Request.Builder()
                .url(groqUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(reqBody)
                .build()

            try {
                Log.d("GrokRepo", "Request URL: ${request.url}")
                Log.d("GrokRepo", "Request headers: ${request.headers}")
                Log.d("GrokRepo", "Request body: ${payload}")
            } catch (_: Throwable) { }

            client.newCall(request).execute().use { resp ->
                val respBody = resp.body?.string().orEmpty()
                Log.d("GrokRepo", "Response code: ${resp.code}")
                Log.d("GrokRepo", "Response body: $respBody")

                if (!resp.isSuccessful) {
                    return Result.failure(Exception("Groq API error: ${resp.code}: $respBody"))
                }

                return try {
                    val json = JSONObject(respBody)
                    val content = json
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                    Result.success(content)
                } catch (_: Throwable) {
                    // If parsing fails, return raw response for visibility.
                    Result.success(respBody)
                }
            }
        } catch (t: Throwable) { Result.failure(t) }
    }

    // Simple connectivity test to a known HTTPS host (GitHub) to help diagnose TLS issues
    fun testConnectivity(): Result<String> {
        return try {
            val request = Request.Builder()
                .url("https://api.github.com/")
                .get()
                .build()

            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) return Result.failure(Exception("Connectivity test failed: ${resp.code}"))
                val text = resp.body?.string() ?: ""
                Result.success("GitHub OK: ${text.take(200)}")
            }
        } catch (t: Throwable) { Result.failure(t) }
    }
}
