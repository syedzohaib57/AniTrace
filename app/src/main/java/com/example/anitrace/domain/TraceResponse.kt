package com.example.anitrace.domain

import kotlinx.serialization.Serializable

@Serializable
data class TraceResponse(
    val result: List<AnimeResult> = emptyList(),
    val error: String? = null
)

@Serializable
data class AnimeResult(
    val anilist: AniListData,
    val filename: String,
    val episode: String? = null,
    val similarity: Double,
    val image: String,
    val video: String,
    val from: Double,
    val to: Double
)

@Serializable
data class AniListData(
    val id: Int,
    val idMal: Int? = null,
    val title: AnimeTitle? = null
)

@Serializable
data class AnimeTitle(
    val native: String? = null,
    val romaji: String? = null,
    val english: String? = null
)
