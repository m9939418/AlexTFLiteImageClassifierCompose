package com.alex.yang.alextfliteimageclassifiercompose.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.alex.yang.alextfliteimageclassifiercompose.domain.model.ImageLabel
import com.alex.yang.alextfliteimageclassifiercompose.domain.repository.ImageClassifierRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/8.
 *
 *
 */
class ImageClassifierRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageClassifierRepository{

    private val classifier: ImageClassifier by lazy {
        val baseOptions = BaseOptions.builder()
            .setNumThreads(2)
            .build()

        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(baseOptions)
            .setMaxResults(5)
            .build()

        ImageClassifier.createFromFileAndOptions(
            context,
            MODEL_FILE,
            options
        )
    }

    override suspend fun classify(bitmap: Bitmap, topK: Int): List<ImageLabel> {
      return  withContext(Dispatchers.Default) {
            val argbBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                bitmap
            }

            val image = TensorImage.fromBitmap(argbBitmap)

            val result: List<Classifications> = classifier.classify(image)
            val categories = result.firstOrNull()?.categories ?: emptyList()

            categories
                .sortedByDescending { it.score }
                .take(topK)
                .map {
                    ImageLabel(
                        label = it.label,
                        score = it.score
                    )
                }
        }
    }

    companion object {
        private const val MODEL_FILE = "efficientnet_lite0.tflite"
    }
}