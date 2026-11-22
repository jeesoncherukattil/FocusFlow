package com.focusflow.app.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class ReelInterceptionService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var lastBlockedPkg: String? = null
    private var lastBlockTime = 0L

    private val alwaysBlock = setOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.zhiliaoapp.musically",
        "com.facebook.katana",
        "com.twitter.android"
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        if (!isFocusActive) return

        val pkg = event.packageName?.toString()?.trim() ?: return
        val shouldBlock = BlockedAppsStore.packages.value.any { it.equals(pkg, true) } || alwaysBlock.contains(pkg)
        if (!shouldBlock) return

        val now = System.currentTimeMillis()
        if (pkg == lastBlockedPkg && now - lastBlockTime < 600) return

        lastBlockedPkg = pkg
        lastBlockTime = now

        val homeOk = performGlobalAction(GLOBAL_ACTION_HOME)
        if (!homeOk) performGlobalAction(GLOBAL_ACTION_BACK)

        handler.post { Toast.makeText(this, "Blocked – stay focused", Toast.LENGTH_SHORT).show() }
    }

    override fun onInterrupt() {}
    override fun onDestroy() { instance = null; super.onDestroy() }

    companion object {
        @Volatile var instance: ReelInterceptionService? = null
        @Volatile var isFocusActive: Boolean = false
    }
}
