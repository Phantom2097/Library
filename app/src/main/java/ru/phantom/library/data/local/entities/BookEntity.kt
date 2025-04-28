package ru.phantom.library.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: Long,
    val author: String,
    val numberOfPages: Int?,
)
