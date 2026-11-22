package com.focusflow.app.service

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class BlockOverlay(private val context: Context) {

    private var overlayView: LinearLayout? = null
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    fun show(packageName: String) {
        if (overlayView != null) return

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#CC1A1A2E"))
            gravity = Gravity.CENTER
            setPadding(64, 64, 64, 64)
        }

        val title = TextView(context).apply {
            text = "Focus Mode"
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val message = TextView(context).apply {
            text = "This app is blocked\n\n$packageName"
            textSize = 16f
            setTextColor(Color.parseColor("#DDFFFFFF"))
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 48)
        }

        val button = Button(context).apply {
            text = "Return to FocusFlow"
            setOnClickListener { hide() }
        }

        layout.addView(title)
        layout.addView(message)
        layout.addView(button)

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        overlayView = layout
        try {
            windowManager.addView(layout, params)
        } catch (_: Exception) {
            overlayView = null
        }
    }

    fun hide() {
        overlayView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
            overlayView = null
        }
    }

    fun isShowing() = overlayView != null
}
