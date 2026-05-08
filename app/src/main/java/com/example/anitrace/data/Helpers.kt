package com.example.anitrace.data

import com.example.anitrace.domain.AnimeResult

fun extractRealImageUrl(input: String): String {
    return try {
        val uri = android.net.Uri.parse(input)

        if (uri.host?.contains("google.com") == true && uri.getQueryParameter("imgurl") != null) {
            uri.getQueryParameter("imgurl") ?: input
        } else {
            input
        }
    } catch (e: Exception) {
        input
    }
}

// Extension to get first episode number from "168|388" or "168"
fun AnimeResult.firstEpisode(): Int? {
    return episode?.split("|")?.firstOrNull()?.trim()?.toIntOrNull()
}

const val baseUrl = "https://api.trace.moe"
