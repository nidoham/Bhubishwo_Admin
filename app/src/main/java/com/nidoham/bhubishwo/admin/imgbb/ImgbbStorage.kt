package com.nidoham.bhubishwo.admin.imgbb

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Handles uploading images to ImgBB.
 *
 * Recommendation: Inject this class using Hilt/Koin rather than using it as a static Object.
 * Example: @Provides fun provideImgbb() = ImgbbRepository("YOUR_API_KEY")
 */
class ImgbbRepository(
    private val apiKey: String
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Increased for large uploads
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val BASE_URL = "https://api.imgbb.com/1/upload"
        private const val MAX_FILE_SIZE = 32 * 1024 * 1024 // 32 MB
    }

    data class ImgbbResult(
        val success: Boolean,
        val url: String?,
        val deleteUrl: String?,
        val thumbUrl: String?,
        val title: String?,
        val error: String? = null
    )

    // ==================== UPLOAD FILE ====================

    suspend fun upload(
        file: File,
        customName: String? = null,
        expirationSeconds: Int? = null
    ): ImgbbResult = withContext(Dispatchers.IO) {
        if (!file.exists() || !file.canRead()) {
            return@withContext ImgbbResult(false, null, null, null, null, "File does not exist or cannot be read")
        }
        if (file.length() > MAX_FILE_SIZE) {
            return@withContext ImgbbResult(false, null, null, null, null, "File exceeds 32MB limit")
        }

        // 1. Build the URL (Only API Key in query params)
        val url = buildBaseUrl()

        // 2. Build Multipart Body
        // Moving 'name' and 'expiration' to the BODY avoids URL encoding issues completely.
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))

        if (!customName.isNullOrBlank()) {
            builder.addFormDataPart("name", customName.trim())
        }
        if (expirationSeconds != null && expirationSeconds in 60..15_552_000) {
            builder.addFormDataPart("expiration", expirationSeconds.toString())
        }

        executeRequest(url, builder.build())
    }

    // ==================== UPLOAD BASE64 ====================

    suspend fun uploadBase64(
        base64: String,
        customName: String? = null,
        expirationSeconds: Int? = null
    ): ImgbbResult = withContext(Dispatchers.IO) {
        val url = buildBaseUrl()

        val builder = FormBody.Builder()
            .add("image", base64) // ImgBB handles base64 strings automatically

        if (!customName.isNullOrBlank()) {
            builder.add("name", customName.trim())
        }
        if (expirationSeconds != null && expirationSeconds in 60..15_552_000) {
            builder.add("expiration", expirationSeconds.toString())
        }

        executeRequest(url, builder.build())
    }

    // ==================== UPLOAD URL ====================

    suspend fun uploadFromUrl(
        imageUrl: String,
        customName: String? = null,
        expirationSeconds: Int? = null
    ): ImgbbResult = withContext(Dispatchers.IO) {
        val url = buildBaseUrl()

        val builder = FormBody.Builder()
            .add("image", imageUrl)

        if (!customName.isNullOrBlank()) {
            builder.add("name", customName.trim())
        }
        if (expirationSeconds != null && expirationSeconds in 60..15_552_000) {
            builder.add("expiration", expirationSeconds.toString())
        }

        executeRequest(url, builder.build())
    }

    // ==================== INTERNAL UTILS ====================

    private fun buildBaseUrl(): HttpUrl {
        return BASE_URL.toHttpUrlOrNull()!!
            .newBuilder()
            .addQueryParameter("key", apiKey)
            .build()
    }

    private fun executeRequest(url: HttpUrl, body: okhttp3.RequestBody): ImgbbResult {
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()

                if (!response.isSuccessful || responseBody.isNullOrBlank()) {
                    return ImgbbResult(
                        success = false,
                        url = null, deleteUrl = null, thumbUrl = null, title = null,
                        error = "HTTP ${response.code}: ${response.message}"
                    )
                }
                parseResponse(responseBody)
            }
        } catch (e: Exception) {
            ImgbbResult(
                success = false,
                url = null, deleteUrl = null, thumbUrl = null, title = null,
                error = "Network Error: ${e.message}"
            )
        }
    }

    private fun parseResponse(jsonString: String): ImgbbResult {
        return try {
            val root = JSONObject(jsonString)
            val success = root.optBoolean("success")

            if (!success) {
                val errorMsg = root.optJSONObject("error")?.optString("message")
                    ?: "Unknown ImgBB Error"
                return ImgbbResult(false, null, null, null, null, errorMsg)
            }

            val data = root.getJSONObject("data")

            ImgbbResult(
                success = true,
                url = data.getString("url"),
                deleteUrl = data.optString("delete_url"),
                thumbUrl = data.optJSONObject("thumb")?.optString("url"),
                title = data.optString("title"),
                error = null
            )
        } catch (e: Exception) {
            ImgbbResult(false, null, null, null, null, "JSON Parsing Error: ${e.message}")
        }
    }
}