package com.example.imagecropper_android.di

import android.content.Context
import androidx.room.Room
import com.example.imagecropper_android.data.photo.local.AppDatabase
import com.example.imagecropper_android.data.photo.local.PhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "app.db").build()

    @Provides
    fun providePhotoDao(db: AppDatabase): PhotoDao = db.photoDao()
}