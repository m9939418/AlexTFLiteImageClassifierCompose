package com.alex.yang.alextfliteimageclassifiercompose.domain.repository

import android.graphics.Bitmap
import com.alex.yang.alextfliteimageclassifiercompose.domain.model.ImageLabel

/**
 * Created by AlexYang on 2025/12/8.
 *
 *
 */
interface ImageClassifierRepository {
    suspend fun classify(bitmap: Bitmap, topK: Int = 3): List<ImageLabel>
}