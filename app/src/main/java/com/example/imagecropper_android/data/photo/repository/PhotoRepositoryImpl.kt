package com.example.imagecropper_android.data.photo.repository

import com.example.imagecropper_android.data.photo.local.PhotoDao
import com.example.imagecropper_android.domain.photo.model.PhotoDomain
import com.example.imagecropper_android.domain.photo.model.toDomain
import com.example.imagecropper_android.domain.photo.model.toEntity
import com.example.imagecropper_android.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photoDao: PhotoDao
) : PhotoRepository {

    override fun getAll(): Flow<List<PhotoDomain>> =
        photoDao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getById(id: String): Flow<PhotoDomain?> =
        photoDao.getById(id).map { it?.toDomain() }

    override suspend fun save(photo: PhotoDomain) {
        photoDao.upsert(photo.toEntity())
    }

    override suspend fun delete(id: String) {
        photoDao.deleteById(id)
    }
}