package com.alex.yang.alextfliteimageclassifiercompose.feature.home

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alex.yang.alextfliteimageclassifiercompose.core.toBitmap
import com.alex.yang.alextfliteimageclassifiercompose.domain.model.ImageLabel
import com.alex.yang.alextfliteimageclassifiercompose.feature.home.component.CameraCapture
import com.alex.yang.alextfliteimageclassifiercompose.ui.theme.AlexTFLiteImageClassifierComposeTheme

/**
 * Created by AlexYang on 2025/12/8.
 *
 *
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeViewModel.UiState,
    onImageSelected: (Bitmap) -> Unit = {}
) {
    val context = LocalContext.current

    // Gallery 選圖
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.toBitmap(context)?.let { bitmap ->
            onImageSelected(bitmap)
        }
    }

    // 相機權限 & 是否顯示 CameraCapture
    var showCamera by remember { mutableStateOf(false) }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showCamera = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        if (showCamera) {
            CameraCapture(
                onImageCaptured = { bitmap ->
                    showCamera = false
                    onImageSelected(bitmap)
                },
                onClose = { showCamera = false }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // 圖片預覽
                    state.selectedBitmap?.let { bitmap ->
                        // 計算原圖寬高比
                        val aspectRatio = remember(bitmap) {
                            if (bitmap.height != 0) {
                                bitmap.width.toFloat() / bitmap.height.toFloat()
                            } else {
                                1f
                            }
                        }

                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(aspectRatio)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(Modifier.height(16.dp))
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ImageSearch,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    text = "請從下方選擇圖片或拍照"
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                    }

                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "分類結果"
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        when {
                            state.isLoading -> {
                                item { Text(text = "推論中…") }
                            }

                            state.error != null -> {
                                item {
                                    Text(
                                        color = MaterialTheme.colorScheme.error,
                                        text = "Error: ${state.error}"
                                    )
                                }
                            }

                            state.labels.isEmpty() -> {
                                item { Text(text = "請選擇圖片或拍照進行分類") }
                            }

                            else -> {
                                items(state.labels) { label ->
                                    ResultRow(label)
                                }
                            }
                        }
                    }
                }


                HorizontalFloatingToolbar(
                    modifier = Modifier
                        .padding(bottom = 60.dp)
                        .align(Alignment.BottomCenter),
                    expanded = true,
                    colors = FloatingToolbarDefaults.standardFloatingToolbarColors()
                ) {
                    listOf(
                        Icons.Default.ImageSearch,
                        Icons.Default.Camera
                    ).forEach { iconRes ->
                        IconButton(
                            onClick = {
                                when (iconRes) {
                                    Icons.Default.ImageSearch -> {
                                        galleryLauncher.launch("image/*")
                                    }

                                    else -> {
                                        cameraPermissionLauncher.launch(
                                            android.Manifest.permission.CAMERA
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = iconRes,
                                contentDescription = null,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(item: ImageLabel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(item.label)
        Text(
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            text = "${(item.score * 100).toInt()}%"
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Light Mode"
)
@Preview(
    showBackground = true,
    showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun HomeScreenPreview() {
    AlexTFLiteImageClassifierComposeTheme {
        HomeScreen(
            state = HomeViewModel.UiState(),
            onImageSelected = { }
        )
    }
}