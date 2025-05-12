package ru.phantom.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import ru.phantom.data.local.entities.DiskEntity

@Dao
interface DiskDao {
    @Query("SELECT * FROM disks WHERE id = :id")
    suspend fun getDiskInfoById(id: Long): DiskEntity
}