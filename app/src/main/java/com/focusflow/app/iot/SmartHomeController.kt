package com.focusflow.app.iot

/**
 * Stub for Tuya / smart home integration.
 * Triggers devices (lights, plugs) when a focus block starts.
 */
class SmartHomeController {

    suspend fun onFocusStart() {
        // TODO: Call Tuya REST API or local SDK
        println("FocusFlow: Triggering smart home devices for focus mode")
    }

    suspend fun onFocusEnd() {
        println("FocusFlow: Restoring normal environment")
    }
}
