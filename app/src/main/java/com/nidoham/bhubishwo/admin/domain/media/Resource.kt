package com.nidoham.bhubishwo.admin.domain.media

import android.net.Uri

data class Resource(
    val id: String,
    val title: String,
    val url: String,
    val tags: Set<String> = emptySet()
) {
    init {
        require(id.isNotBlank())
        require(title.isNotBlank())
        require(runCatching { Uri.parse(url) }.isSuccess) { "Invalid URL: $url" }
    }

    val uri: Uri get() = Uri.parse(url)
    val hasTags: Boolean get() = tags.isNotEmpty()
}