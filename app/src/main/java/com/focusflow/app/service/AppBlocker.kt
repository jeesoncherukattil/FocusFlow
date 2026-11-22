package com.focusflow.app.service

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings

class AppBlocker(private val context: Context) {

    private val usageStats = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false
    private var blockedPackages: Set<String> = emptySet()

    fun startMonitoring(packages: Set<String>) {
        blockedPackages = packages
        if (isMonitoring) return
        isMonitoring = true
        handler.post(checkRunnable)
    }

    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacks(checkRunnable)
    }

    private val checkRunnable = object : Runnable {
        override fun run() {
            if (!isMonitoring) return
            val foreground = getForegroundPackage()
            if (foreground != null && blockedPackages.contains(foreground)) {
                val intent = Intent(context, BlockScreenActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("blocked_package", foreground)
                }
                context.startActivity(intent)
            }
            handler.postDelayed(this, 1500)
        }
    }

    private fun getForegroundPackage(): String? {
        val end = System.currentTimeMillis()
        val start = end - 5000
        val stats = usageStats.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end)
        return stats.maxByOrNull { it.lastTimeUsed }?.packageName
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

    fun requestUsagePermission() {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
    }
}
