package com.notsatria.bajet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.notsatria.bajet.ui.BajetApp
import com.notsatria.bajet.ui.screen.home.HomeScreen
import com.notsatria.bajet.ui.theme.BajetTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BajetTheme {
               BajetApp()
            }
        }
    }
}

