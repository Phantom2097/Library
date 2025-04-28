package ru.phantom.library.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "newspapers")
data class NewspaperEntity(
    @PrimaryKey val ownerId: Long,
    val issueNumber: Int,
    val month: Int
)
