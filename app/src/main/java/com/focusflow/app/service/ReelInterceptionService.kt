package com.focusflow.app.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast

class ReelInterceptionService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var lastPkg: String? = null

    private val alwaysBlock = setOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.zhiliaoapp.musically"
    )

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        showToast("FocusFlow service ON")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val pkg = event.packageName?.toString()?.trim() ?: return
        if (pkg == lastPkg) return
        lastPkg = pkg

        val inStore = BlockedAppsStore.packages.value.any { it.equals(pkg, ignoreCase = true) }
        val shouldBlock = isFocusActive && (inStore || alwaysBlock.contains(pkg))

        showToast("pkg=$pkg focus=$isFocusActive store=$inStore block=$shouldBlock")

        if (shouldBlock) {
            val homeOk = performGlobalAction(GLOBAL_ACTION_HOME)
            if (!homeOk) performGlobalAction(GLOBAL_ACTION_BACK)
            showToast("Tried block on $pkg (home=$homeOk)")
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
