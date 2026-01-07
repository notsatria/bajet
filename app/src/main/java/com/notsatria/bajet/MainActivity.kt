package com.notsatria.bajet

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.lifecycleScope
import com.notsatria.bajet.ui.BajetApp
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.DateUtils
import com.notsatria.bajet.utils.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()
    private var isFirstLanguageLoad = true
    private var currentLanguageCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        lifecycleScope.launch {
            viewModel.language.collect { languageCode ->
                // Skip the first emission to avoid recreate on app start
                if (isFirstLanguageLoad) {
                    isFirstLanguageLoad = false
                    currentLanguageCode = languageCode
                    setLocale(this@MainActivity, languageCode)
                } else if (languageCode != currentLanguageCode) {
                    // Only recreate if language actually changed
                    currentLanguageCode = languageCode
                    setLocale(this@MainActivity, languageCode)
                    recreate()
                }
            }
        }
        
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
    
    private fun setLocale(context: Context, languageCode: String) {
        val locale = Helper.getLocale(languageCode)
        Locale.setDefault(locale)
        
        // Update DateUtils locale reactively
        DateUtils.updateLocale(locale)
        
        val config = context.resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            config.setLocale(locale)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }
        
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
