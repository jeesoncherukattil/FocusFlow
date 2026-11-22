package com.focusflow.app.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.focusflow.app.ui.overlay.ReelCounterOverlay

class ReelInterceptionService : AccessibilityService() {
    private lateinit var overlay: ReelCounterOverlay
    private var reelCount = 0
    private var lastPackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlay = ReelCounterOverlay(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val pkg = event.packageName?.toString() ?: return
        if (pkg !in TARGET_PACKAGES) {
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
    override fun onDestroy() { overlay.hide(); super.onDestroy() }

    companion object {
        val TARGET_PACKAGES = setOf(
            "com.instagram.android",
            "com.google.android.youtube",
            "com.zhiliaoapp.musically",
            "com.facebook.katana"
        )
    }
}
