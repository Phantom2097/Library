package ru.phantom.library.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey (autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val availability: Boolean,
    val time: Long
)
