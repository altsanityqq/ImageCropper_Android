package com.example.imagecropper_android.di

import com.example.imagecropper_android.data.photo.repository.PhotoRepositoryImpl
import com.example.imagecropper_android.domain.photo.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(impl: PhotoRepositoryImpl): PhotoRepository
}