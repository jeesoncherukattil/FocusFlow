package com.focusflow.app.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple in-memory store for the apps the user wants blocked during focus.
 * Shared between BlockerScreen and PomodoroViewModel.
 */
object BlockedAppsStore {
    private val _packages = MutableStateFlow(
        setOf("com.instagram.android", "com.google.android.youtube")
    )
    val packages: StateFlow<Set<String>> = _packages.asStateFlow()

    fun setPackages(pkgs: Set<String>) {
        _packages.value = pkgs
    }

    fun toggle(packageName: String) {
        val current = _packages.value
        _packages.value = if (current.contains(packageName)) {
            current - packageName
        } else {
            current + packageName
        }
    }
}
