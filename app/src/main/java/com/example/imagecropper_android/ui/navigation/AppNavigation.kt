package com.example.imagecropper_android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imagecropper_android.ui.screens.ImageCropScreen
import com.example.imagecropper_android.ui.screens.ImagesListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.ImagesList
    ) {
        composable<Route.ImagesList> {
            ImagesListScreen(
                onAddClick = {
                    navController.navigate(Route.ImageCrop)
                }
            )
        }
        composable<Route.ImageCrop> {
            ImageCropScreen(
                onImageSaved = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}