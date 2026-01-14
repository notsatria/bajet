package com.notsatria.bajet.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.widget.Toast
import androidx.core.net.toUri
import com.notsatria.bajet.R

data class DeviceInfo(
    val brand: String,
    val model: String,
    val device: String,
    val androidVersion: String,
    val sdkInt: Int
)

fun getBasicDeviceInfo(): DeviceInfo {
    return DeviceInfo(
        brand = Build.BRAND,
        model = Build.MODEL,
        device = Build.DEVICE,
        androidVersion = Build.VERSION.RELEASE ?: "Unknown",
        sdkInt = Build.VERSION.SDK_INT
    )
}

fun getRamInfo(context: Context): Pair<Long, Long> {
    val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val totalRam = memoryInfo.totalMem
    val availableRam = memoryInfo.availMem

    return totalRam to availableRam
}

fun getStorageInfo(): Pair<Long, Long> {
    val statFs = StatFs(Environment.getDataDirectory().path)

    val totalBytes = statFs.totalBytes
    val availableBytes = statFs.availableBytes

    return totalBytes to availableBytes
}

fun formatSize(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024 * 1024)
    return String.format(LOCALE_ID, "%.2f GB", gb)
}

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "N/A"
    } catch (e: Exception) {
        "N/A"
    }
}

fun openFeedbackEmail(context: Context) {
    val deviceInfo = getBasicDeviceInfo()
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.developer_email)))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.bajet_app_feedback))
        putExtra(
            Intent.EXTRA_TEXT, """
            ---
            Device Info:
            Brand: ${deviceInfo.brand}
            Model: ${deviceInfo.model}
            Device: ${deviceInfo.device}
            Android Version: ${deviceInfo.androidVersion}
            SDK Int: ${deviceInfo.sdkInt}
            App Version: ${getAppVersion(context)}
            Storage: ${formatSize(getStorageInfo().first)} total, ${formatSize(getStorageInfo().second)} available
            RAM: ${formatSize(getRamInfo(context).first)} total, ${formatSize(getRamInfo(context).second)} available
        """.trimIndent()
        )
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.bajet_app_feedback_extra))
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Send Feedback"))
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "No email client found",
            Toast.LENGTH_SHORT
        ).show()
    }
}
