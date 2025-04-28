package ru.phantom.library.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey (autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val availability: Boolean,
    @ColumnInfo(name = "item_type")
    val itemType: String? = null,
    val time: Long
) {
    companion object {
        const val BOOK = "Book"
        const val DISK = "Disk"
        const val NEWSPAPER = "Newspaper"
    }
}