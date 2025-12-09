package com.alex.yang.alextfliteimageclassifiercompose.feature.home.di

import com.alex.yang.alextfliteimageclassifiercompose.data.repository.ImageClassifierRepositoryImpl
import com.alex.yang.alextfliteimageclassifiercompose.domain.repository.ImageClassifierRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by AlexYang on 2025/12/8.
 *
 *
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {
    @Binds
    @Singleton
    abstract fun bindImageClassifierRepository(
        impl: ImageClassifierRepositoryImpl
    ): ImageClassifierRepository
}