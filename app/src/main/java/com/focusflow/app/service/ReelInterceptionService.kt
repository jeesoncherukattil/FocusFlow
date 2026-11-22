package com.focusflow.app.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class ReelInterceptionService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var lastToastPkg: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        showToast("FocusFlow service ON")
        Log.d("FocusFlowA11y", "Service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val pkg = event.packageName?.toString() ?: return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && lastToastPkg != pkg) {
            lastToastPkg = pkg
            showToast("Window: $pkg")
            Log.d("FocusFlowA11y", "Window: $pkg | focus=$isFocusActive | blocked=${BlockedAppsStore.packages.value}")
        }

        if (isFocusActive && BlockedAppsStore.packages.value.contains(pkg)) {
            showToast("BLOCKING $pkg")
            performGlobalAction(GLOBAL_ACTION_HOME)
        }
    }

    private fun showToast(msg: String) {
        handler.post { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    override fun onInterrupt() {}
    override fun onDestroy() { instance = null; super.onDestroy() }

    companion object {
        @Volatile var instance: ReelInterceptionService? = null
        @Volatile var isFocusActive: Boolean = false
    }
}
