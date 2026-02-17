package com.nidoham.bhubishwo.admin.domain.media

import java.net.URI

data class Resource(
    val id: String,
    val title: String,
    val url: String,
    val tags: Set<String> = emptySet()
) {
    init {
        require(id.isNotBlank()) { "id cannot be blank" }
        require(title.isNotBlank()) { "title cannot be blank" }

        // Use standard Java URI for validation to keep this class Pure Kotlin
        try {
            val parsed = URI.create(url)
            require(!parsed.scheme.isNullOrBlank() && !parsed.host.isNullOrBlank()) {
                "Invalid URL (must have a scheme and host): $url"
            }
        } catch (e: IllegalArgumentException) {
            // Re-throw with your specific message or let the original bubble up
            throw IllegalArgumentException("Invalid URL format: $url", e)
        }
    }

    val hasTags: Boolean get() = tags.isNotEmpty()
}