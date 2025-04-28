package ru.phantom.library.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "all_in_one")
data class AllInOneEntity(
    @Embedded val itemEntity: ItemEntity,
    @Embedded val bookEntity: BookEntity,
    @Embedded val diskEntity: DiskEntity,
    @Embedded val newspaperEntity: NewspaperEntity,
    @ColumnInfo(name = "add_time")
    val addTime: Long
)
