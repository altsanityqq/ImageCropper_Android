package com.example.imagecropper_android.util

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import com.example.imagecropper_android.ui.model.CropRect
import com.example.imagecropper_android.ui.model.PointF
import com.example.imagecropper_android.ui.model.RectInt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun imageBoundsInContainer(bitmap: Bitmap, containerSize: IntSize): Rect? {
    val cw = containerSize.width
    val ch = containerSize.height
    if (cw <= 0 || ch <= 0) return null
    val ow = bitmap.width
    val oh = bitmap.height
    if (ow <= 0 || oh <= 0) return null

    val scale = min(cw / ow.toFloat(), ch / oh.toFloat())
    val drawnW = ow * scale
    val drawnH = oh * scale
    val dx = (cw - drawnW) / 2f
    val dy = (ch - drawnH) / 2f
    return Rect(dx, dy, dx + drawnW, dy + drawnH)
}

fun screenToOriginal(
    originalW: Int,
    originalH: Int,
    containerW: Int,
    containerH: Int,
    screenRect: Rect
): RectInt {
    val scale = min(
        containerW / originalW.toFloat(),
        containerH / originalH.toFloat()
    )
    val drawnW = originalW * scale
    val drawnH = originalH * scale
    val dx = (containerW - drawnW) / 2f
    val dy = (containerH - drawnH) / 2f

    val leftPx = ((screenRect.left - dx) / scale).roundToInt().coerceIn(0, originalW)
    val topPx  = ((screenRect.top  - dy) / scale).roundToInt().coerceIn(0, originalH)
    val wPx    = (screenRect.width  / scale).roundToInt().coerceAtLeast(1)
    val hPx    = (screenRect.height / scale).roundToInt().coerceAtLeast(1)

    val safeW = min(wPx, (originalW - leftPx)).coerceAtLeast(1)
    val safeH = min(hPx, (originalH - topPx)).coerceAtLeast(1)
    return RectInt(leftPx, topPx, safeW, safeH)
}

fun cropBitmapFor(
    bitmap: Bitmap,
    containerSize: IntSize,
    screenRect: Rect
): Bitmap? {
    val bounds = imageBoundsInContainer(bitmap, containerSize) ?: return null
    val clamped = Rect(
        max(screenRect.left, bounds.left),
        max(screenRect.top, bounds.top),
        min(screenRect.right, bounds.right),
        min(screenRect.bottom, bounds.bottom)
    )
    if (clamped.width <= 0f || clamped.height <= 0f) return null

    val crop = screenToOriginal(
        originalW = bitmap.width,
        originalH = bitmap.height,
        containerW = containerSize.width,
        containerH = containerSize.height,
        screenRect = clamped
    )
    if (crop.w <= 0 || crop.h <= 0) return null
    if (crop.x < 0 || crop.y < 0) return null
    if (crop.x + crop.w > bitmap.width || crop.y + crop.h > bitmap.height) return null

    return Bitmap.createBitmap(bitmap, crop.x, crop.y, crop.w, crop.h)
}

fun Rect.toCropRectOriginal(
    bitmap: Bitmap,
    containerSize: IntSize
): CropRect? {
    val bounds = imageBoundsInContainer(bitmap, containerSize) ?: return null
    val clamped = Rect(
        max(left, bounds.left),
        max(top, bounds.top),
        min(right, bounds.right),
        min(bottom, bounds.bottom)
    )
    if (clamped.width <= 0f || clamped.height <= 0f) return null

    val crop = screenToOriginal(
        originalW = bitmap.width,
        originalH = bitmap.height,
        containerW = containerSize.width,
        containerH = containerSize.height,
        screenRect = clamped
    )
    val tl = PointF(crop.x.toFloat(), crop.y.toFloat())
    val br = PointF((crop.x + crop.w).toFloat(), (crop.y + crop.h).toFloat())
    return CropRect(topLeft = tl, bottomRight = br)
}