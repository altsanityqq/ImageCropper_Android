package com.example.imagecropper_android.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.imagecropper_android.R
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun ImagesScreen() {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember(imageUri) {
        mutableStateOf(imageUri?.let { loadBitmap(context, it) })
    }

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        imageUri = uri
    }

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri?.let { uri ->
                context.contentResolver.openInputStream(uri)?.use { input ->
                    bitmap = BitmapFactory.decodeStream(input)
                }
            }
        }
    }

    val reqPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
            val photoFile = File.createTempFile("capture_", ".jpg", imagesDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            imageUri = uri
            takePicture.launch(uri)
        }
    }

    Scaffold(
        floatingActionButton = {
            MultipleFloatingActionButton(
                onAttachClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onTakePhotoClick = {
                    reqPermission.launch(Manifest.permission.CAMERA)
                },
            )
        }
    ) { innerPaddings ->
        bitmap?.let {
            ImagesScreenContent(
                modifier = Modifier.padding(innerPaddings),
                bitmap = it,
            )
        }
    }
}

fun loadBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input)
            }
        }
    } catch (t: Throwable) {
        null
    }
}


@Composable
private fun MultipleFloatingActionButton(
    onAttachClick: () -> Unit,
    onTakePhotoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(isExpanded) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        isExpanded = !isExpanded
                        onAttachClick()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_attach_file_24),
                        contentDescription = null
                    )
                }

                FloatingActionButton(
                    onClick = {
                        isExpanded = !isExpanded
                        onTakePhotoClick()
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_photo_camera_24),
                        contentDescription = null
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier.size(80.dp),
            onClick = { isExpanded = !isExpanded },
        ) {
            AnimatedContent(isExpanded) {
                if (it) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(R.drawable.outline_close_24),
                        contentDescription = null
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(R.drawable.outline_image_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun ImagesScreenContent(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var cropRectScreen by remember { mutableStateOf<Rect?>(null) }
    var cropped by remember { mutableStateOf<Bitmap?>(null) }

    fun imageBoundsInContainer(): Rect? {
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
        originalW: Int, originalH: Int,
        containerW: Int, containerH: Int,
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
        val topPx = ((screenRect.top - dy) / scale).roundToInt().coerceIn(0, originalH)
        val sizePx = (screenRect.width / scale).roundToInt()

        val safeSize = min(sizePx, min(originalW - leftPx, originalH - topPx)).coerceAtLeast(1)
        return RectInt(leftPx, topPx, safeSize, safeSize)
    }

    fun recomputeCrop() {
        val r = cropRectScreen ?: return
        val bounds = imageBoundsInContainer() ?: return
        val clamped = Rect(
            max(r.left, bounds.left),
            max(r.top, bounds.top),
            min(r.right, bounds.right),
            min(r.bottom, bounds.bottom)
        )
        if (clamped.width <= 0f || clamped.height <= 0f) return

        val crop = screenToOriginal(
            originalW = bitmap.width,
            originalH = bitmap.height,
            containerW = containerSize.width,
            containerH = containerSize.height,
            screenRect = clamped
        )
        if (crop.w > 0 && crop.h > 0 &&
            crop.x >= 0 && crop.y >= 0 &&
            crop.x + crop.w <= bitmap.width &&
            crop.y + crop.h <= bitmap.height
        ) {
            cropped = Bitmap.createBitmap(bitmap, crop.x, crop.y, crop.w, crop.h)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFF727272))
                .onGloballyPositioned {
                    containerSize = it.size
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds(),
                contentScale = ContentScale.Fit
            )
            val imgBounds = imageBoundsInContainer()
            CutOutOverlay(
                containerSize = containerSize,
                imageBounds = imgBounds,
                onCropRectChanged = { cropRectScreen = it },
                onCropCommitted = { cropRectScreen = it; recomputeCrop() }
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Original size = ${bitmap.width}px × ${bitmap.height}px",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        cropped?.let { c ->
            Image(
                bitmap = c.asImageBitmap(),
                contentDescription = "Cropped",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Cropped preview (${c.width}×${c.height})",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private data class RectInt(val x: Int, val y: Int, val w: Int, val h: Int)

enum class DragTarget { BODY, TL, TR, BL, BR, NONE }

@Composable
private fun CutOutOverlay(
    modifier: Modifier = Modifier,
    containerSize: IntSize,
    imageBounds: Rect?,
    onCropRectChanged: (Rect) -> Unit,
    onCropCommitted: (Rect) -> Unit
) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { 1.dp.toPx() }
    val handleRadius = with(density) { 10.dp.toPx() }
    val minSidePxDesign = with(density) { 96.dp.toPx() }

    var squareTopLeft by remember { mutableStateOf(Offset.Zero) }
    var squareSide by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(containerSize, imageBounds) {
        imageBounds ?: return@LaunchedEffect
        val bw = imageBounds.width
        val bh = imageBounds.height
        if (bw > 0f && bh > 0f) {
            val side = min(bw, bh) * 0.6f
            squareSide = side
            val left = imageBounds.left + (bw - side) / 2f
            val top = imageBounds.top + (bh - side) / 2f
            squareTopLeft = Offset(left, top)
            onCropRectChanged(Rect(left, top, left + side, top + side))
        }
    }

    val topLeftState by rememberUpdatedState(squareTopLeft)
    val sideState by rememberUpdatedState(squareSide)
    val handleRState by rememberUpdatedState(handleRadius)
    val boundsState by rememberUpdatedState(imageBounds)

    fun currentRect(): Rect =
        Rect(topLeftState.x, topLeftState.y, topLeftState.x + sideState, topLeftState.y + sideState)

    fun minSideForBounds(b: Rect): Float =
        min(minSidePxDesign, min(b.width, b.height)).coerceAtLeast(1f)

    fun clampTopLeftToBoundsUnsafe(topLeft: Offset, side: Float, b: Rect): Offset {
        val minX = b.left
        val minY = b.top
        val maxX = b.right - side
        val maxY = b.bottom - side
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

    fun sideMaxForHandle(anchor: Offset, which: DragTarget, b: Rect): Float = when (which) {
        DragTarget.TL -> min(anchor.x - b.left, anchor.y - b.top)
        DragTarget.TR -> min(b.right - anchor.x, anchor.y - b.top)
        DragTarget.BL -> min(anchor.x - b.left, b.bottom - anchor.y)
        DragTarget.BR -> min(b.right - anchor.x, b.bottom - anchor.y)
        else -> Float.POSITIVE_INFINITY
    }.coerceAtLeast(0f)

    fun topLeftFromAnchor(anchor: Offset, side: Float, which: DragTarget): Offset = when (which) {
        DragTarget.TL -> Offset(anchor.x - side, anchor.y - side)
        DragTarget.TR -> Offset(anchor.x, anchor.y - side)
        DragTarget.BL -> Offset(anchor.x - side, anchor.y)
        DragTarget.BR -> Offset(anchor.x, anchor.y)
        else -> squareTopLeft
    }

    var currentDragTarget by remember { mutableStateOf(DragTarget.NONE) }
    var dragAccum by remember { mutableStateOf(Offset.Zero) }
    var handleStart by remember { mutableStateOf(Offset.Zero) }
    var anchor by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(containerSize, imageBounds) {
                detectDragGestures(
                    onDragStart = { downPos ->
                        val b = boundsState ?: return@detectDragGestures
                        val rect = currentRect()
                        currentDragTarget = hitTest(downPos, rect, handleRState)
                        dragAccum = Offset.Zero

                        when (currentDragTarget) {
                            DragTarget.TL -> {
                                anchor = Offset(rect.right, rect.bottom); handleStart =
                                    Offset(rect.left, rect.top)
                            }

                            DragTarget.TR -> {
                                anchor = Offset(rect.left, rect.bottom); handleStart =
                                    Offset(rect.right, rect.top)
                            }

                            DragTarget.BL -> {
                                anchor = Offset(rect.right, rect.top); handleStart =
                                    Offset(rect.left, rect.bottom)
                            }

                            DragTarget.BR -> {
                                anchor = Offset(rect.left, rect.top); handleStart =
                                    Offset(rect.right, rect.bottom)
                            }

                            DragTarget.BODY, DragTarget.NONE -> Unit
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consumePositionChange()
                        val b = boundsState ?: return@detectDragGestures

                        when (currentDragTarget) {
                            DragTarget.BODY -> {
                                val newTopLeft = clampTopLeftToBoundsUnsafe(
                                    topLeftState + dragAmount,
                                    sideState,
                                    b
                                )
                                squareTopLeft = newTopLeft
                                onCropRectChanged(
                                    Rect(
                                        newTopLeft.x,
                                        newTopLeft.y,
                                        newTopLeft.x + sideState,
                                        newTopLeft.y + sideState
                                    )
                                )
                            }

                            DragTarget.TL, DragTarget.TR, DragTarget.BL, DragTarget.BR -> {
                                dragAccum += dragAmount
                                val handleCand = handleStart + dragAccum
                                val sideRaw =
                                    max(abs(handleCand.x - anchor.x), abs(handleCand.y - anchor.y))

                                val sideMax = sideMaxForHandle(anchor, currentDragTarget, b)
                                val minSide = minSideForBounds(b)

                                if (sideMax <= 0f) return@detectDragGestures
                                val sideClamped =
                                    if (sideMax < minSide) {
                                        sideMax
                                    } else {
                                        sideRaw.coerceIn(minSide, sideMax)
                                    }

                                val tl = topLeftFromAnchor(anchor, sideClamped, currentDragTarget)
                                squareTopLeft = clampTopLeftToBoundsUnsafe(tl, sideClamped, b)
                                squareSide = sideClamped

                                onCropRectChanged(
                                    Rect(
                                        squareTopLeft.x,
                                        squareTopLeft.y,
                                        squareTopLeft.x + squareSide,
                                        squareTopLeft.y + squareSide
                                    )
                                )
                            }

                            DragTarget.NONE -> Unit
                        }
                    },
                    onDragEnd = {
                        currentDragTarget = DragTarget.NONE
                        val r = currentRect()
                        onCropCommitted(r)
                    },
                    onDragCancel = {
                        currentDragTarget = DragTarget.NONE
                        val r = currentRect()
                        onCropCommitted(r)
                    }
                )
            }
    ) {
        drawRect(
            color = Color.White.copy(alpha = .5f),
            topLeft = squareTopLeft,
            size = Size(squareSide, squareSide),
            style = Stroke(width = strokeWidth)
        )

        val r = Rect(
            squareTopLeft.x,
            squareTopLeft.y,
            squareTopLeft.x + squareSide,
            squareTopLeft.y + squareSide
        )
        drawCircle(
            color = Color.White.copy(alpha = .5f),
            center = r.center,
            radius = r.height / 2,
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

@Preview
@Composable
private fun ImageScreenPreview() {
    ImagesScreen()
}