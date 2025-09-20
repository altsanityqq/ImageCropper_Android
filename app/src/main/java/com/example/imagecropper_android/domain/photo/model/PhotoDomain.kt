package com.example.imagecropper_android.domain.photo.model

import com.example.imagecropper_android.data.photo.model.CropRectEmbeddable
import com.example.imagecropper_android.data.photo.model.PhotoEntity
import com.example.imagecropper_android.data.photo.model.PointFEmbeddable

data class PhotoDomain(
    val id: String,
    val originalUri: String,
    val squareCrop: CropRectDomain,
    val rectCrop: CropRectDomain,
    val createdAt: Long
)

data class CropRectDomain(
    val topLeft: PointFDomain,
    val bottomRight: PointFDomain
)

data class PointFDomain(
    val x: Float,
    val y: Float
)

fun PhotoDomain.toEntity(): PhotoEntity =
    PhotoEntity(
        id = id,
        originalUri = originalUri,
        squareCrop = squareCrop.toEmb(),
        rectCrop = rectCrop.toEmb(),
        createdAt = createdAt
    )

fun PhotoEntity.toDomain(): PhotoDomain =
    PhotoDomain(
        id = id,
        originalUri = originalUri,
        squareCrop = squareCrop.toDomain(),
        rectCrop = rectCrop.toDomain(),
        createdAt = createdAt
    )

private fun CropRectDomain.toEmb(): CropRectEmbeddable =
    CropRectEmbeddable(
        topLeft = topLeft.toEmb(),
        bottomRight = bottomRight.toEmb()
    )

private fun CropRectEmbeddable.toDomain(): CropRectDomain =
    CropRectDomain(
        topLeft = topLeft.toDomain(),
        bottomRight = bottomRight.toDomain()
    )

private fun PointFDomain.toEmb(): PointFEmbeddable = PointFEmbeddable(x, y)
private fun PointFEmbeddable.toDomain(): PointFDomain = PointFDomain(x, y)