package com.example.imagecropper_android

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ImagesScreen() {

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    val takePhoto = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            Log.d("PhotoPicker", "Got Bitmap with size: ${bitmap.width}x${bitmap.height}")
        } else {
            Log.d("PhotoPicker", "No photo taken")
        }
    }

    Scaffold(
        floatingActionButton = {
            MultipleFloatingActionButton(
                onAttachClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onTakePhotoClick = {
                    takePhoto.launch(null)
                },
            )
        }
    ) { innerPaddings ->
        ImagesScreenContent(
            modifier = Modifier.padding(innerPaddings)
        )
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

    }
}

@Preview
@Composable
private fun ImageScreenPreview() {
    ImagesScreen()
}