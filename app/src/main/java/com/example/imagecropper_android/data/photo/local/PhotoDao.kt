package com.example.imagecropper_android.data.photo.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.imagecropper_android.data.photo.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE id = :id LIMIT 1")
    fun getById(id: String): Flow<PhotoEntity?>

    @Upsert
    suspend fun upsert(entity: PhotoEntity)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deleteById(id: String)
}