package com.focusflow.app.ui.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class ReelCounterOverlay(private val context: Context) {
    private var overlayView: android.view.View? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    fun show(reelsCount: Int, remainingSeconds: Int) {
        if (overlayView != null) return

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 32
            y = 120
        }

        val textView = TextView(context).apply {
            text = "Reels: $reelsCount\nBreak: ${remainingSeconds / 60}:${remainingSeconds % 60}"
            setPadding(24, 16, 24, 16)
            setBackgroundColor(0xCC000000.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
        }

        overlayView = textView
        windowManager.addView(textView, params)
    }

    fun update(reelsCount: Int, remainingSeconds: Int) {
        (overlayView as? TextView)?.text =
            "Reels: $reelsCount\nBreak: ${remainingSeconds / 60}:${String.format("%02d", remainingSeconds % 60)}"
    }

    fun hide() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
