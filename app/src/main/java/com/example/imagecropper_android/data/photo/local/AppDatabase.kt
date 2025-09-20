package com.example.imagecropper_android.data.photo.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.imagecropper_android.data.photo.model.PhotoEntity

@Database(
    entities = [PhotoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
}