package com.example.imagecropper_android.ui.components

import android.graphics.Bitmap
import coil3.size.Size
import coil3.transform.Transformation
import com.example.imagecropper_android.ui.model.CropRect
import kotlin.math.max
import kotlin.math.min

class RectCropTransformation(
    private val crop: CropRect
) : Transformation() {

    override val cacheKey: String =
        "RectCrop(${crop.topLeft.x},${crop.topLeft.y},${crop.bottomRight.x},${crop.bottomRight.y})"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        var left = crop.topLeft.x.toInt()
        var top  = crop.topLeft.y.toInt()
        var w = (crop.bottomRight.x - crop.topLeft.x).toInt()
        var h = (crop.bottomRight.y - crop.topLeft.y).toInt()

        left = left.coerceIn(0, input.width - 1)
        top  = top .coerceIn(0, input.height - 1)
        val right  = min(left + w, input.width)
        val bottom = min(top + h, input.height)
        w = max(1, right - left)
        h = max(1, bottom - top)

        if (left == 0 && top == 0 && w == input.width && h == input.height) return input

        return Bitmap.createBitmap(input, left, top, w, h)
    }
}