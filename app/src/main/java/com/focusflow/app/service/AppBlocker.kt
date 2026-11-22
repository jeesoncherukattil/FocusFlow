package com.focusflow.app.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings

class AppBlocker(private val context: Context) {

    private val usageStats = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val handler = Handler(Looper.getMainLooper())
    private val overlay = BlockOverlay(context)
    private var isMonitoring = false
    private var blockedPackages: Set<String> = emptySet()
    private var lastBlocked: String? = null

    fun startMonitoring(packages: Set<String>) {
        blockedPackages = packages
        if (isMonitoring) return
        isMonitoring = true
        handler.post(checkRunnable)
    }

    fun stopMonitoring() {
        isMonitoring = false
        lastBlocked = null
        overlay.hide()
        handler.removeCallbacks(checkRunnable)
    }

    private val checkRunnable = object : Runnable {
        override fun run() {
            if (!isMonitoring) return
            val foreground = getForegroundPackage()
            if (foreground != null && blockedPackages.contains(foreground)) {
                if (lastBlocked != foreground || !overlay.isShowing()) {
                    overlay.show(foreground)
                    lastBlocked = foreground
                }
            } else {
                lastBlocked = null
            }
            handler.postDelayed(this, 700)
        }
    }

    private fun getForegroundPackage(): String? {
        val end = System.currentTimeMillis()
        val start = end - 8_000
        val events = usageStats.queryEvents(start, end)
        val event = UsageEvents.Event()
        var last: String? = null
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
            ) {
                last = event.packageName
            }
        }
        return last
    }

    fun hasUsagePermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
        val mode = appOps.checkOpNoThrow(
            android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == android.app.AppOpsManager.MODE_ALLOWED
    }

    fun hasOverlayPermission(): Boolean = Settings.canDrawOverlays(context)

    fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            android.net.Uri.parse("package:${context.packageName}")
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
