package ru.phantom.library.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disks")
data class DiskEntity(
    @PrimaryKey val id: Long,
    val type: String
)
