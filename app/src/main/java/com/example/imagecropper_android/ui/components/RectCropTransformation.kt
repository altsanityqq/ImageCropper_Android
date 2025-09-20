package com.example.imagecropper_android.ui.components

import android.graphics.Bitmap
import coil3.size.Size
import coil3.transform.Transformation
import com.example.imagecropper_android.ui.model.CropRect

class RectCropTransformation(
    private val crop: CropRect
) : Transformation() {

    override val cacheKey: String =
        "RectCrop(${crop.topLeft.x},${crop.topLeft.y},${crop.bottomRight.x},${crop.bottomRight.y})"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val left = crop.topLeft.x.toInt().coerceAtLeast(0)
        val top  = crop.topLeft.y.toInt().coerceAtLeast(0)
        val w = (crop.bottomRight.x - crop.topLeft.x).toInt().coerceAtLeast(1)
        val h = (crop.bottomRight.y - crop.topLeft.y).toInt().coerceAtLeast(1)

        if (left + w > input.width || top + h > input.height) return input

        return Bitmap.createBitmap(input, left, top, w, h)
    }
}