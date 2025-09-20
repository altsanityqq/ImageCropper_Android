package com.example.imagecropper_android.ui.model

import com.example.imagecropper_android.domain.photo.model.CropRectDomain
import com.example.imagecropper_android.domain.photo.model.PhotoDomain
import com.example.imagecropper_android.domain.photo.model.PointFDomain

data class PhotoUi(
    val id: String,
    val originalUri: String,
    val squareCrop: CropRect,
    val rectCrop: CropRect,
    val createdAt: Long
)

fun PhotoUi.toDomain(): PhotoDomain =
    PhotoDomain(
        id = id,
        originalUri = originalUri,
        squareCrop = squareCrop.toDomain(),
        rectCrop = rectCrop.toDomain(),
        createdAt = createdAt
    )

fun PhotoDomain.toUi(): PhotoUi =
    PhotoUi(
        id = id,
        originalUri = originalUri,
        squareCrop = squareCrop.toUi(),
        rectCrop = rectCrop.toUi(),
        createdAt = createdAt
    )

private fun CropRect.toDomain(): CropRectDomain =
    CropRectDomain(topLeft = topLeft.toDomain(), bottomRight = bottomRight.toDomain())

private fun PointF.toDomain(): PointFDomain = PointFDomain(x = x, y = y)

private fun CropRectDomain.toUi(): CropRect =
    CropRect(topLeft = topLeft.toUi(), bottomRight = bottomRight.toUi())

private fun PointFDomain.toUi(): PointF = PointF(x = x, y = y)
