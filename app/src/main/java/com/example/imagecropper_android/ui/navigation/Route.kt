package com.example.imagecropper_android.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object ImagesList : Route

    @Serializable
    data object ImageCrop : Route
}