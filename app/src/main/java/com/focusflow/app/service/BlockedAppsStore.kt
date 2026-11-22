package com.focusflow.app.service

import android.app.Application
import com.focusflow.app.FocusFlowApp
import com.focusflow.app.data.BlockedApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object BlockedAppsStore {
    private val _packages = MutableStateFlow(
        setOf("com.instagram.android", "com.google.android.youtube")
    )
    val packages: StateFlow<Set<String>> = _packages.asStateFlow()

    private var initialized = false

    fun init(application: Application) {
        if (initialized) return
        initialized = true
        val dao = (application as FocusFlowApp).database.blockedAppDao()
        CoroutineScope(Dispatchers.IO).launch {
            dao.getEnabledPackages().collect { list ->
                if (list.isNotEmpty()) {
                    _packages.value = list.toSet()
                }
            }
        }
    }

    fun toggle(application: Application, packageName: String) {
        val current = _packages.value
        val newSet = if (current.contains(packageName)) current - packageName else current + packageName
        _packages.value = newSet
        val dao = (application as FocusFlowApp).database.blockedAppDao()
        CoroutineScope(Dispatchers.IO).launch {
            if (newSet.contains(packageName)) {
                dao.insert(BlockedApp(packageName, true))
            } else {
                dao.remove(packageName)
            }
        }
    }
}
