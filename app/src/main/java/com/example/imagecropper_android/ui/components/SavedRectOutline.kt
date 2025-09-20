package com.example.imagecropper_android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun SavedRectOutline(rect: Rect?, color: Color) {
    if (rect == null) return
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = color.copy(alpha = 0.6f),
            topLeft = Offset(rect.left, rect.top),
            size = Size(rect.width, rect.height),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}