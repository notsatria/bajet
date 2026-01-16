package com.notsatria.bajet.utils

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import timber.log.Timber

class InAppUpdateManager(private val activity: Activity) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private var onUpdateDownloaded: (() -> Unit)? = null

    companion object {
        const val REQUEST_CODE_UPDATE = 1001
        private const val UPDATE_PRIORITY_IMMEDIATE_THRESHOLD = 4
    }

    /**
     * Set callback for when update is downloaded (for showing snackbar in Compose)
     */
    fun setOnUpdateDownloadedCallback(callback: () -> Unit) {
        onUpdateDownloaded = callback
    }

    /**
     * Check if an update is available and start the update flow
     */
    fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                val updateType = getUpdateType(appUpdateInfo)
                
                if (appUpdateInfo.isUpdateTypeAllowed(updateType)) {
                    Timber.i("Update available. Type: ${if (updateType == AppUpdateType.FLEXIBLE) "FLEXIBLE" else "IMMEDIATE"}")
                    startUpdate(appUpdateInfo, updateType)
                }
            } else {
                Timber.d("No update available")
            }
        }.addOnFailureListener { exception ->
            Timber.e(exception, "Failed to check for updates")
        }
    }

    /**
     * Determine update type based on priority
     * Priority 4-5: Immediate (critical updates)
     * Priority 0-3: Flexible (regular updates)
     */
    private fun getUpdateType(appUpdateInfo: AppUpdateInfo): Int {
        return if (appUpdateInfo.updatePriority() >= UPDATE_PRIORITY_IMMEDIATE_THRESHOLD) {
            AppUpdateType.IMMEDIATE
        } else {
            AppUpdateType.FLEXIBLE
        }
    }

    /**
     * Start the update flow
     */
    private fun startUpdate(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                activity,
                AppUpdateOptions.newBuilder(updateType).build(),
                REQUEST_CODE_UPDATE
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to start update flow")
        }
    }

    /**
     * Call this in onResume to handle update completion and resumption
     */
    fun onResume() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            // Handle flexible update completion
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                Timber.i("Update downloaded, triggering callback")
                onUpdateDownloaded?.invoke()
            }

            // Handle immediate update resumption (if user left during update)
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                Timber.i("Resuming immediate update")
                startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
            }
        }
    }

    /**
     * Complete the flexible update installation
     */
    fun completeUpdate() {
        Timber.i("Completing update installation")
        appUpdateManager.completeUpdate()
    }

    /**
     * Register listener for update progress
     */
    fun registerListener(listener: InstallStateUpdatedListener) {
        appUpdateManager.registerListener(listener)
    }

    /**
     * Unregister listener to prevent memory leaks
     */
    fun unregisterListener(listener: InstallStateUpdatedListener) {
        appUpdateManager.unregisterListener(listener)
    }
}
