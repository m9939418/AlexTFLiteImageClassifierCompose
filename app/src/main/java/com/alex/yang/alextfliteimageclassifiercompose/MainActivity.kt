package com.alex.yang.alextfliteimageclassifiercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alex.yang.alextfliteimageclassifiercompose.feature.home.HomeScreen
import com.alex.yang.alextfliteimageclassifiercompose.feature.home.HomeViewModel
import com.alex.yang.alextfliteimageclassifiercompose.ui.theme.AlexTFLiteImageClassifierComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlexTFLiteImageClassifierComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = hiltViewModel<HomeViewModel>()
                    val state by viewModel.uiState.collectAsStateWithLifecycle()
                    HomeScreen(
                        state = state,
                        onImageSelected = viewModel::onImageSelected
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AlexTFLiteImageClassifierComposeTheme {
        Greeting("Android")
    }
}