package ru.phantom.library.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.phantom.library.data.local.entities.DiskEntity

@Dao
interface DiskDao {
    @Insert
    suspend fun insertDisk(diskEntity: DiskEntity)

    @Query("SELECT * FROM disks WHERE id = :id")
    suspend fun getDiskInfoById(id: Long): DiskEntity
}