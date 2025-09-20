package com.example.imagecropper_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.imagecropper_android.ui.navigation.AppNavigation
import com.example.imagecropper_android.ui.theme.ImageCropper_AndroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageCropper_AndroidTheme {
                AppNavigation()
            }
        }
    }
}