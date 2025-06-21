package com.notsatria.bajet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.lifecycleScope
import com.notsatria.bajet.ui.BajetApp
import com.notsatria.bajet.ui.theme.BajetTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Locale.setDefault(Locale.getDefault())
        enableEdgeToEdge()
        lifecycleScope.launch {
            viewModel.theme.collect { themeMode ->
                setContent {
                    val darkTheme = when (themeMode) {
                        getString(R.string.light) -> false
                        getString(R.string.dark) -> true
                        else -> isSystemInDarkTheme()
                    }
                    BajetTheme(darkTheme = darkTheme) {
                        BajetApp()
                    }
                }
            }
        }
    }
}

