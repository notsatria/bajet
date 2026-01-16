package com.notsatria.bajet

import android.app.Activity
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
import com.notsatria.bajet.utils.InAppUpdateManager
import com.notsatria.bajet.utils.ThemeMode
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel by viewModels<MainViewModel>()
    private var isFirstLanguageLoad = true
    private var currentLanguageCode = ""
    
    private lateinit var inAppUpdateManager: InAppUpdateManager
    
    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                val bytesDownloaded = state.bytesDownloaded()
                val totalBytesToDownload = state.totalBytesToDownload()
                val progress = (bytesDownloaded * 100 / totalBytesToDownload).toInt()
                Timber.d("Update downloading: $progress%")
            }
            InstallStatus.DOWNLOADED -> {
                Timber.i("Update downloaded successfully")
            }
            InstallStatus.INSTALLED -> {
                Timber.i("Update installed successfully")
            }
            InstallStatus.FAILED -> {
                Timber.e("Update failed with error code: ${state.installErrorCode()}")
            }
            else -> {
                Timber.d("Update status: ${state.installStatus()}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize in-app update manager
        inAppUpdateManager = InAppUpdateManager(this)
        inAppUpdateManager.registerListener(installStateUpdatedListener)
        
        // Check for updates on app start
        inAppUpdateManager.checkForUpdate()
        
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
                val finalThemeMode = if (themeMode == null) {
                    viewModel.setDefaultTheme()
                    ThemeMode.SYSTEM.value
                } else {
                    themeMode
                }
                setContent {
                    val darkTheme = when (finalThemeMode) {
                        ThemeMode.LIGHT.value -> false
                        ThemeMode.DARK.value -> true
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
    
    override fun onResume() {
        super.onResume()
        inAppUpdateManager.onResume()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        inAppUpdateManager.unregisterListener(installStateUpdatedListener)
    }
    
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == InAppUpdateManager.REQUEST_CODE_UPDATE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Timber.i("Update flow started successfully")
                }
                Activity.RESULT_CANCELED -> {
                    Timber.w("Update canceled by user")
                }
                else -> {
                    Timber.e("Update failed with result code: $resultCode")
                }
            }
        }
    }
}
