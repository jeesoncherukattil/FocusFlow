package com.focusflow.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.focusflow.app.ui.overlay.ReelCounterOverlay

class ReelInterceptionService : AccessibilityService() {

    private lateinit var overlay: ReelCounterOverlay
    private var lastBlocked: String? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        overlay = ReelCounterOverlay(this)
        instance = this
        Toast.makeText(this, "FocusFlow Accessibility connected", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val pkg = event.packageName?.toString() ?: return

        if (isFocusActive && BlockedAppsStore.packages.value.contains(pkg)) {
            if (lastBlocked != pkg) {
                lastBlocked = pkg
                performGlobalAction(GLOBAL_ACTION_HOME)
                handler.post {
                    Toast.makeText(this, "Blocked: $pkg", Toast.LENGTH_SHORT).show()
                }
                try {
                    val intent = Intent(this, BlockScreenActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        putExtra("blocked_package", pkg)
                    }
                    startActivity(intent)
                } catch (_: Exception) {}
            }
        } else {
            lastBlocked = null
        }

        if (pkg in TARGET_PACKAGES && event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            overlay.show(0, 0)
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
