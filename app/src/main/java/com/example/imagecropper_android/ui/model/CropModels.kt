package com.example.imagecropper_android.ui.model

data class CropRect(
    val topLeft: PointF,
    val bottomRight: PointF
)

data class PointF(
    val x: Float,
    val y: Float
)

enum class CropAspect(val aspect: Float) {
    Square(1f),
    Ratio3x4(3f / 4f),
}

enum class DragTarget { BODY, TL, TR, BL, BR, NONE }

data class RectInt(val x: Int, val y: Int, val w: Int, val h: Int)