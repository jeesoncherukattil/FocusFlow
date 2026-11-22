package com.focusflow.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.focusflow.app.ui.overlay.ReelCounterOverlay

class ReelInterceptionService : AccessibilityService() {

    private lateinit var overlay: ReelCounterOverlay
    private var reelCount = 0
    private var lastPackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlay = ReelCounterOverlay(this)
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val pkg = event.packageName?.toString() ?: return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (BlockedAppsStore.packages.value.contains(pkg) && isFocusActive) {
                val intent = Intent(this, BlockScreenActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra("blocked_package", pkg)
                }
                startActivity(intent)
            }
        }

        val isTarget = pkg in TARGET_PACKAGES
        if (!isTarget) {
            if (lastPackage in TARGET_PACKAGES) overlay.hide()
            lastPackage = pkg
            return
        }
        lastPackage = pkg
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            reelCount++
            overlay.show(reelCount, 300)
            overlay.update(reelCount, 300)
        }
    }

    override fun onInterrupt() { overlay.hide() }
    override fun onDestroy() { overlay.hide(); instance = null; super.onDestroy() }

    companion object {
        @Volatile var instance: ReelInterceptionService? = null
        @Volatile var isFocusActive: Boolean = false
        val TARGET_PACKAGES = setOf(
            "com.instagram.android",
            "com.google.android.youtube",
            "com.zhiliaoapp.musically",
            "com.facebook.katana"
        )
    }
}
