package ru.phantom.library.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NewspaperEntity(
    @PrimaryKey val ownerId: Long,
    val issueNumber: Int
)
