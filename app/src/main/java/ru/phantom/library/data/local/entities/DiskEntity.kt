package ru.phantom.library.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.phantom.library.domain.entities.library.disk.Type

@Entity
data class DiskEntity(
    @PrimaryKey val ownerId: Long,
    val type: Type
)
