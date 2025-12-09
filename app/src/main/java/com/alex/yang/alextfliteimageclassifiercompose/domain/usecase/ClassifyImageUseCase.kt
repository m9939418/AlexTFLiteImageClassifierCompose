package com.alex.yang.alextfliteimageclassifiercompose.domain.usecase

import android.graphics.Bitmap
import com.alex.yang.alextfliteimageclassifiercompose.domain.repository.ImageClassifierRepository
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/8.
 *
 *
 */
class ClassifyImageUseCase @Inject constructor(
    private val repository: ImageClassifierRepository
) {
    suspend operator fun invoke(bitmap: Bitmap, topK: Int = 3) =
        repository.classify(bitmap, topK)
}