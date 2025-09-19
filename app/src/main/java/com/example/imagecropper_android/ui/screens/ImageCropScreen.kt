package com.example.imagecropper_android.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.imagecropper_android.R
import com.example.imagecropper_android.ui.components.AspectToggle
import com.example.imagecropper_android.ui.components.CutOutOverlayFixedAspect
import com.example.imagecropper_android.ui.components.SavedRectOutline
import com.example.imagecropper_android.ui.models.CropAspect
import com.example.imagecropper_android.ui.models.PhotoDto
import com.example.imagecropper_android.util.cropBitmapFor
import com.example.imagecropper_android.util.imageBoundsInContainer
import com.example.imagecropper_android.util.loadBitmap
import com.example.imagecropper_android.util.toCropRectOriginal
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCropScreen(
    onSave: (PhotoDto) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
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

    val bmp = bitmap
    val uriStr = imageUri?.toString()
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = { Text(text = stringResource(R.string.crop_image)) },
            )
        },
        floatingActionButton = {
            if (bmp == null && uriStr == null) {
                MultipleFloatingActionButton(
                    onAttachClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onTakePhotoClick = {
                        reqPermission.launch(Manifest.permission.CAMERA)
                    }
                )
            }
        }
    ) { innerPaddings ->
        if (bmp != null && uriStr != null) {
            ImageCropContent(
                modifier = Modifier.padding(innerPaddings),
                bitmap = bmp,
                originalUri = uriStr,
                onSave = onSave
            )
        }
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
private fun ImageCropContent(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    originalUri: String,
    onSave: (PhotoDto) -> Unit
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var activeAspect by remember { mutableStateOf(CropAspect.Square) }
    var activeRectScreen by remember { mutableStateOf<Rect?>(null) }
    var savedSquareScreen by remember { mutableStateOf<Rect?>(null) }
    var savedRectScreen by remember { mutableStateOf<Rect?>(null) }
    var squarePreview by remember { mutableStateOf<Bitmap?>(null) }
    var rectPreview by remember { mutableStateOf<Bitmap?>(null) }

    val bothSelected = savedSquareScreen != null && savedRectScreen != null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(12.dp)
    ) {
        AspectToggle(
            activeAspect = activeAspect,
            onAspectChange = { activeAspect = it }
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFF727272))
                .onGloballyPositioned { containerSize = it.size },
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clipToBounds(),
                contentScale = ContentScale.Fit
            )

            val imgBounds = imageBoundsInContainer(bitmap, containerSize)

            SavedRectOutline(savedSquareScreen, color = Color.Cyan)
            SavedRectOutline(savedRectScreen, color = Color.Magenta)

            CutOutOverlayFixedAspect(
                containerSize = containerSize,
                imageBounds = imgBounds,
                aspect = activeAspect,
                onCropRectChanged = { activeRectScreen = it },
                onCropCommitted = { activeRectScreen = it }
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val r = activeRectScreen ?: return@Button
                when (activeAspect) {
                    CropAspect.Square -> {
                        savedSquareScreen = r
                        squarePreview = cropBitmapFor(bitmap, containerSize, r)
                    }
                    CropAspect.Ratio3x4 -> {
                        savedRectScreen = r
                        rectPreview = cropBitmapFor(bitmap, containerSize, r)
                    }
                }
            }) {
                Text(
                    when (activeAspect) {
                        CropAspect.Square -> "Confirm 1:1"
                        CropAspect.Ratio3x4 -> "Confirm 3:4"
                    }
                )
            }

            OutlinedButton(onClick = {
                when (activeAspect) {
                    CropAspect.Square -> { savedSquareScreen = null; squarePreview = null }
                    CropAspect.Ratio3x4 -> { savedRectScreen = null; rectPreview = null }
                }
            }) {
                Text("Reset ${if (activeAspect == CropAspect.Square) "1:1" else "3:4"}")
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Original size = ${bitmap.width} × ${bitmap.height}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val sq = squarePreview
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (sq != null) {
                        Image(
                            bitmap = sq.asImageBitmap(),
                            contentDescription = "Square Preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        PreviewPlaceholder("1:1")
                    }
                }
                Text("Square", style = MaterialTheme.typography.bodySmall)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val r34 = rectPreview
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .aspectRatio(3f / 4f),
                    contentAlignment = Alignment.Center
                ) {
                    if (r34 != null) {
                        Image(
                            bitmap = r34.asImageBitmap(),
                            contentDescription = "3:4 Preview",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        PreviewPlaceholder("3:4", rounded = 12)
                    }
                }
                Text("Rect 3:4", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val squareCrop = savedSquareScreen?.toCropRectOriginal(bitmap, containerSize)
                val rectCrop = savedRectScreen?.toCropRectOriginal(bitmap, containerSize)
                if (squareCrop != null && rectCrop != null) {
                    val dto = PhotoDto(
                        id = UUID.randomUUID().toString(),
                        originalUri = originalUri,
                        squareCrop = squareCrop,
                        rectCrop = rectCrop,
                        createdAt = System.currentTimeMillis()
                    )
                    onSave(dto)
                }
            },
            enabled = bothSelected,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}

@Composable
private fun PreviewPlaceholder(label: String, rounded: Int = 50) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(rounded))
            .background(Color.Black.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
private fun ImageCropScreenPreview() {
    ImageCropScreen()
}