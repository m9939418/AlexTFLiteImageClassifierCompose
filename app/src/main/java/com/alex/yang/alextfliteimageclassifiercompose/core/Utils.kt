package com.alex.yang.alextfliteimageclassifiercompose.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlin.math.max

/**
 * Created by AlexYang on 2025/12/8.
 *
 *
 */
fun Uri.toBitmap(context: Context, maxSize: Int = 1080): Bitmap? =
    runCatching {
        val raw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, this)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                // 避免拿到 HARDWARE config
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        }

        raw
            .ensureArgb8888()
            .scaleDown(maxSize)
    }.getOrNull()

fun Bitmap.ensureArgb8888(): Bitmap {
    return if (config == Bitmap.Config.ARGB_8888) {
        this
    } else {
        copy(Bitmap.Config.ARGB_8888, false)
    }
}

fun Bitmap.scaleDown(maxSize: Int): Bitmap {
    val maxEdge = max(width, height).toFloat()
    if (maxEdge <= maxSize) return this

    val scale = maxSize / maxEdge
    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()
    return Bitmap.createScaledBitmap(this, newWidth, newHeight, true)
}