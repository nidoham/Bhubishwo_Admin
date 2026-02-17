package com.nidoham.bhubishwo.admin.imgbb

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * ImgBB Storage singleton. Initialize with [init] in Application.onCreate()
 * Uses suspend functions for safe coroutine-based networking
 */
object ImgbbStorage {

    private const val BASE_URL = "https://api.imgbb.com/1/upload"
    private const val MAX_FILE_SIZE = 32 * 1024 * 1024 // 32MB

    private lateinit var apiKey: String
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    data class UploadResult(
        val success: Boolean,
        val url: String?,
        val deleteUrl: String?,
        val thumbUrl: String?,
        val errorMessage: String?,
        val rawResponse: String?
    )

    /**
     * Initialize with API key
     */
    fun init(key: String) {
        apiKey = key
    }

    /**
     * Upload image file to ImgBB - suspend function for coroutines
     */
    suspend fun upload(
        file: File,
        name: String? = null,
        expiration: Int? = null
    ): UploadResult = withContext(Dispatchers.IO) {
        checkInitialized()

        if (!file.exists())
            return@withContext UploadResult(false, null, null, null, "File does not exist", null)

        if (file.length() > MAX_FILE_SIZE)
            return@withContext UploadResult(false, null, null, null, "File size exceeds 32MB limit", null)

        // Build URL with API key as query parameter
        val url = buildUrl(name, expiration)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        execute(request)
    }

    /**
     * Upload Base64 image - suspend function
     */
    suspend fun uploadBase64(
        base64: String,
        name: String? = null,
        expiration: Int? = null
    ): UploadResult = withContext(Dispatchers.IO) {
        checkInitialized()

        val url = buildUrl(name, expiration)

        val formBody = FormBody.Builder()
            .add("image", base64)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        execute(request)
    }

    /**
     * Upload from URL - suspend function
     */
    suspend fun uploadFromUrl(
        imageUrl: String,
        name: String? = null,
        expiration: Int? = null
    ): UploadResult = withContext(Dispatchers.IO) {
        checkInitialized()

        val url = buildUrl(name, expiration)

        val formBody = FormBody.Builder()
            .add("image", imageUrl)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        execute(request)
    }

    /**
     * Build URL with API key and optional parameters
     */
    private fun buildUrl(name: String?, expiration: Int?): String {
        val builder = StringBuilder(BASE_URL)
            .append("?key=").append(apiKey)

        name?.let { builder.append("&name=").append(it) }
        expiration?.let {
            if (it in 60..15552000) builder.append("&expiration=").append(it)
        }

        return builder.toString()
    }

    private fun execute(request: Request): UploadResult {
        return try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (!response.isSuccessful || body == null) {
                    return UploadResult(
                        false, null, null, null,
                        "HTTP ${response.code}: ${response.message}", body
                    )
                }
                parse(body)
            }
        } catch (e: IOException) {
            UploadResult(false, null, null, null, "Network error: ${e.message}", null)
        }
    }

    private fun parse(json: String): UploadResult {
        return try {
            val root = JSONObject(json)

            val isSuccess = root.optBoolean("success", false)
            val statusCode = root.optInt("status", 0)

            if (!isSuccess || statusCode != 200) {
                val errorObj = root.optJSONObject("error")
                val errorMessage = errorObj?.optString("message")
                    ?: root.optString("error", "Unknown error")
                        .ifBlank { "HTTP $statusCode" }
                return UploadResult(false, null, null, null, errorMessage, json)
            }

            val data = root.getJSONObject("data")
            UploadResult(
                success = true,
                url = data.optString("url").ifBlank { null },
                deleteUrl = data.optString("delete_url").ifBlank { null },
                thumbUrl = data.optJSONObject("thumb")?.optString("url")?.ifBlank { null },
                errorMessage = null,
                rawResponse = json
            )
        } catch (e: Exception) {
            UploadResult(false, null, null, null, "Parse error: ${e.message}", json)
        }
    }

    private fun checkInitialized() {
        check(::apiKey.isInitialized) {
            "ImgbbStorage not initialized. Call init() in Application.onCreate()"
        }
    }
}