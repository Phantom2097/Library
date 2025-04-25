package ru.phantom.library.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookEntity(
    @PrimaryKey val ownerId: Int,
    val author: String,
    val numberOfPages: Int?,
)
