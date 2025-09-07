package com.example.imagecropper_android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.imagecropper_android.ui.models.CropAspect
import com.example.imagecropper_android.ui.models.DragTarget
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun CutOutOverlayFixedAspect(
    modifier: Modifier = Modifier,
    containerSize: IntSize,
    imageBounds: Rect?,
    aspect: CropAspect,
    onCropRectChanged: (Rect) -> Unit,
    onCropCommitted: (Rect) -> Unit
) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { 1.dp.toPx() }
    val handleRadius = with(density) { 10.dp.toPx() }
    val minSidePxDesign = with(density) { 96.dp.toPx() }
    val aspectF = aspect.aspect

    var rectTopLeft by remember { mutableStateOf(Offset.Zero) }
    var rectHeight by remember { mutableStateOf(0f) }

    LaunchedEffect(containerSize, imageBounds, aspect) {
        val b = imageBounds ?: return@LaunchedEffect
        val bw = b.width
        val bh = b.height
        if (bw > 0f && bh > 0f) {
            val maxH = min(bh, bw / aspectF)
            val initH = maxH * 0.6f
            rectHeight = initH.coerceAtLeast(1f)
            val rectW = rectHeight * aspectF
            val left = b.left + (bw - rectW) / 2f
            val top  = b.top  + (bh - rectHeight) / 2f
            rectTopLeft = Offset(left, top)
            onCropRectChanged(Rect(left, top, left + rectW, top + rectHeight))
        }
    }

    fun currentRect(): Rect {
        val w = rectHeight * aspectF
        return Rect(rectTopLeft.x, rectTopLeft.y, rectTopLeft.x + w, rectTopLeft.y + rectHeight)
    }

    fun minHeightForBounds(b: Rect): Float {
        val minByDesign = minSidePxDesign
        val minByFit = min(b.height, b.width / aspectF)
        return min(minByDesign, minByFit).coerceAtLeast(1f)
    }

    fun clampTopLeft(topLeft: Offset, height: Float, b: Rect): Offset {
        val w = height * aspectF
        val minX = b.left
        val minY = b.top
        val maxX = b.right - w
        val maxY = b.bottom - height
        val x = if (maxX >= minX) topLeft.x.coerceIn(minX, maxX) else minX
        val y = if (maxY >= minY) topLeft.y.coerceIn(minY, maxY) else minY
        return Offset(x, y)
    }

    fun hitTest(pos: Offset, r: Rect, handleR: Float): DragTarget {
        val tl = Offset(r.left, r.top)
        val tr = Offset(r.right, r.top)
        val bl = Offset(r.left, r.bottom)
        val br = Offset(r.right, r.bottom)
        val thr = handleR * 1.2f
        return when {
            (pos - tl).getDistance() <= thr -> DragTarget.TL
            (pos - tr).getDistance() <= thr -> DragTarget.TR
            (pos - bl).getDistance() <= thr -> DragTarget.BL
            (pos - br).getDistance() <= thr -> DragTarget.BR
            pos.x in r.left..r.right && pos.y in r.top..r.bottom -> DragTarget.BODY
            else -> DragTarget.NONE
        }
    }

    var currentDragTarget by remember { mutableStateOf(DragTarget.NONE) }
    var dragAccum by remember { mutableStateOf(Offset.Zero) }
    var handleStart by remember { mutableStateOf(Offset.Zero) }
    var anchor by remember { mutableStateOf(Offset.Zero) }

    val boundsState by rememberUpdatedState(imageBounds)
    val handleRState by rememberUpdatedState(handleRadius)

    Canvas(
        modifier = modifier
            .pointerInput(containerSize, imageBounds, aspect) {
                detectDragGestures(
                    onDragStart = { downPos ->
                        val b = boundsState ?: return@detectDragGestures
                        val r = currentRect()
                        currentDragTarget = hitTest(downPos, r, handleRState)
                        dragAccum = Offset.Zero

                        when (currentDragTarget) {
                            DragTarget.TL -> { anchor = Offset(r.right, r.bottom); handleStart = Offset(r.left,  r.top) }
                            DragTarget.TR -> { anchor = Offset(r.left,  r.bottom); handleStart = Offset(r.right, r.top) }
                            DragTarget.BL -> { anchor = Offset(r.right, r.top);    handleStart = Offset(r.left,  r.bottom) }
                            DragTarget.BR -> { anchor = Offset(r.left,  r.top);    handleStart = Offset(r.right, r.bottom) }
                            DragTarget.BODY, DragTarget.NONE -> Unit
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consumePositionChange()
                        val b = boundsState ?: return@detectDragGestures

                        when (currentDragTarget) {
                            DragTarget.BODY -> {
                                val newTopLeft = clampTopLeft(rectTopLeft + dragAmount, rectHeight, b)
                                rectTopLeft = newTopLeft
                                onCropRectChanged(currentRect())
                            }

                            DragTarget.TL, DragTarget.TR, DragTarget.BL, DragTarget.BR -> {
                                dragAccum += dragAmount
                                val cand = handleStart + dragAmount

                                val dx = abs(cand.x - anchor.x)
                                val dy = abs(cand.y - anchor.y)
                                val candWidth  = max(dx, dy)
                                val candHeight = candWidth / aspectF

                                val maxH = min(b.height, b.width / aspectF)
                                val minH = minHeightForBounds(b)

                                val newH = candHeight.coerceIn(minH, maxH)
                                val tl = when (currentDragTarget) {
                                    DragTarget.TL -> Offset(anchor.x - newH * aspectF, anchor.y - newH)
                                    DragTarget.TR -> Offset(anchor.x,                     anchor.y - newH)
                                    DragTarget.BL -> Offset(anchor.x - newH * aspectF, anchor.y)
                                    DragTarget.BR -> Offset(anchor.x,                     anchor.y)
                                    else -> rectTopLeft
                                }
                                rectTopLeft = clampTopLeft(tl, newH, b)
                                rectHeight = newH

                                onCropRectChanged(currentRect())
                            }

                            DragTarget.NONE -> Unit
                        }
                    },
                    onDragEnd = {
                        currentDragTarget = DragTarget.NONE
                        onCropCommitted(currentRect())
                    },
                    onDragCancel = {
                        currentDragTarget = DragTarget.NONE
                        onCropCommitted(currentRect())
                    }
                )
            }
    ) {
        val r = currentRect()

        drawRect(
            color = Color.White.copy(alpha = .5f),
            topLeft = Offset(r.left, r.top),
            size = Size(r.width, r.height),
            style = Stroke(width = strokeWidth)
        )

        listOf(
            Offset(r.left, r.top),
            Offset(r.right, r.top),
            Offset(r.left, r.bottom),
            Offset(r.right, r.bottom)
        ).forEach { c ->
            drawCircle(color = Color.White, radius = handleRadius, center = c)
            drawCircle(
                color = Color.Black.copy(alpha = 0.5f),
                radius = handleRadius,
                center = c,
                style = Stroke(width = 2f)
            )
        }
    }
}