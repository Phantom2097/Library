package ru.phantom.library.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemEntity(
    @PrimaryKey (autoGenerate = true)
    val id: Int,
    val name: String,
    val availability: Boolean,
)
