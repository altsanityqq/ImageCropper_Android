package com.example.imagecropper_android.domain.photo.repository

import com.example.imagecropper_android.domain.photo.model.PhotoDomain
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getAll(): Flow<List<PhotoDomain>>
    fun getById(id: String): Flow<PhotoDomain?>
    suspend fun save(photo: PhotoDomain)
    suspend fun delete(id: String)
}