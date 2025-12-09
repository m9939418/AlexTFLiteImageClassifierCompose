package com.alex.yang.alextfliteimageclassifiercompose.feature.home

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.yang.alextfliteimageclassifiercompose.domain.model.ImageLabel
import com.alex.yang.alextfliteimageclassifiercompose.domain.usecase.ClassifyImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by AlexYang on 2025/12/8.
 *
 *
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: ClassifyImageUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState())
    val uiState = _uiState.asStateFlow()

    fun onImageSelected(bitmap: Bitmap) {
        // 1. 先更新 UI 狀態：存 bitmap、清空舊結果、顯示 loading
        _uiState.update {
            it.copy(
                selectedBitmap = bitmap,
                labels = emptyList(),
                isLoading = true,
                error = null
            )
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = useCase(bitmap)
                _uiState.update {
                    it.copy(
                        labels = result,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    data class UiState(
        val labels: List<ImageLabel> = emptyList(),
        val selectedBitmap: Bitmap? = null,
        val isLoading: Boolean = false,
        val error: String? = null
    )
}