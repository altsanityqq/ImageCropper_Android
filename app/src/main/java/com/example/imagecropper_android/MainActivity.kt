package com.example.imagecropper_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.imagecropper_android.ui.screens.ImageCropScreen
import com.example.imagecropper_android.ui.theme.ImageCropper_AndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageCropper_AndroidTheme {
                ImageCropScreen()
            }
        }
    }
}