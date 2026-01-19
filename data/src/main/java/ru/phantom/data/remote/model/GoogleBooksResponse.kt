package ru.phantom.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// Использовался https://json2kt.com/
@Serializable
data class GoogleBooksResponse(
    @SerialName("items") val items: ArrayList<Items> = arrayListOf()
)

@Serializable
data class IndustryIdentifiers(
    @SerialName("type") val type: String? = null,
    @SerialName("identifier") var identifier: String? = null
)

@Serializable
data class VolumeInfo(
    @SerialName("title") val title: String? = null,
    @SerialName("authors") val authors: ArrayList<String> = arrayListOf(),
    @SerialName("pageCount") var pageCount: Int? = null,
    @SerialName("industryIdentifiers") val industryIdentifiers: ArrayList<IndustryIdentifiers> = arrayListOf()
)

@Serializable
data class Items(
    @SerialName("volumeInfo") val volumeInfo: VolumeInfo? = VolumeInfo()
)