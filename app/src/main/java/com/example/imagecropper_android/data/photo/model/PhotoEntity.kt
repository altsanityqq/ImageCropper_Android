package com.example.imagecropper_android.data.photo.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
    val originalUri: String,
    @Embedded(prefix = "sq_") val squareCrop: CropRectEmbeddable,
    @Embedded(prefix = "rc_") val rectCrop: CropRectEmbeddable,
    val createdAt: Long
)

data class CropRectEmbeddable(
    @Embedded(prefix = "tl_") val topLeft: PointFEmbeddable,
    @Embedded(prefix = "br_") val bottomRight: PointFEmbeddable
)

data class PointFEmbeddable(
    val x: Float,
    val y: Float
)