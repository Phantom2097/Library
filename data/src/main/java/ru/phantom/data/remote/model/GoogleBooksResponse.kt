package ru.phantom.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// Использовался https://json2kt.com/
@Serializable
data class GoogleBooksResponse(
    @SerialName("items") var items: ArrayList<Items> = arrayListOf()
)

@Serializable
data class IndustryIdentifiers(
    @SerialName("type") var type: String? = null,
    @SerialName("identifier") var identifier: String? = null
)

@Serializable
data class VolumeInfo(
    @SerialName("title") var title: String? = null,
    @SerialName("authors") var authors: ArrayList<String> = arrayListOf(),
    @SerialName("pageCount") var pageCount: Int? = null,
    @SerialName("industryIdentifiers") var industryIdentifiers: ArrayList<IndustryIdentifiers> = arrayListOf()
)

@Serializable
data class Items(
    @SerialName("volumeInfo") var volumeInfo: VolumeInfo? = VolumeInfo()
)