package com.focusflow.app.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.focusflow.app.FocusFlowApp
import com.focusflow.app.data.PomodoroSession
import com.focusflow.app.iot.SmartHomeController
import com.focusflow.app.service.AppBlocker
import com.focusflow.app.service.BlockedAppsStore
import com.focusflow.app.service.PomodoroService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

data class TimerState(
    val isRunning: Boolean = false,
    val isFocusPhase: Boolean = true,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val currentTag: String = "Study",
    val completedSessions: Int = 0,
    val progress: Float = 1f
)

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as FocusFlowApp).database
    private val smartHome = SmartHomeController()
    private val appBlocker = AppBlocker(application)

    init {
        BlockedAppsStore.init(application)
    }

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var focusMinutes = 25
    private var breakMinutes = 5

    fun startOrPause() {
        if (_timerState.value.isRunning) pause() else start()
    }

    private fun start() {
        val state = _timerState.value
        if (state.isFocusPhase) {
            viewModelScope.launch { smartHome.onFocusStart() }
            appBlocker.startMonitoring(BlockedAppsStore.packages.value)
            val intent = Intent(getApplication(), PomodoroService::class.java)
            getApplication<Application>().startForegroundService(intent)
        }
        _timerState.value = state.copy(isRunning = true)
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                delay(1000)
                val s = _timerState.value
                val progress = s.remainingSeconds.toFloat() / s.totalSeconds
                _timerState.value = s.copy(
                    remainingSeconds = s.remainingSeconds - 1,
                    progress = progress
                )
            }
            if (_timerState.value.remainingSeconds <= 0) onPhaseComplete()
        }
    }

    private fun pause() {
        timerJob?.cancel()
        appBlocker.stopMonitoring()
        getApplication<Application>().stopService(Intent(getApplication(), PomodoroService::class.java))
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    private fun onPhaseComplete() {
        val state = _timerState.value
        if (state.isFocusPhase) {
            viewModelScope.launch { smartHome.onFocusEnd() }
            appBlocker.stopMonitoring()
            getApplication<Application>().stopService(Intent(getApplication(), PomodoroService::class.java))
            saveSession(true)
            _timerState.value = state.copy(
                isRunning = false,
                isFocusPhase = false,
                remainingSeconds = breakMinutes * 60,
                totalSeconds = breakMinutes * 60,
                completedSessions = state.completedSessions + 1,
                progress = 1f
            )
        } else {
            _timerState.value = state.copy(
                isRunning = false,
                isFocusPhase = true,
                remainingSeconds = focusMinutes * 60,
                totalSeconds = focusMinutes * 60,
                progress = 1f
            )
        }
    }

    private fun saveSession(completed: Boolean) {
        viewModelScope.launch {
            try {
                db.sessionDao().insert(
                    PomodoroSession(
                        activityTag = _timerState.value.currentTag,
                        focusMinutes = focusMinutes,
                        breakMinutes = breakMinutes,
                        completed = completed,
                        startTime = Instant.now().minusSeconds(focusMinutes * 60L),
                        endTime = Instant.now()
                    )
                )
            } catch (_: Exception) { }
        }
    }

    fun setTag(tag: String) {
        if (!_timerState.value.isRunning) {
            _timerState.value = _timerState.value.copy(currentTag = tag)
        }
    }

    fun reset() {
        timerJob?.cancel()
        appBlocker.stopMonitoring()
        getApplication<Application>().stopService(Intent(getApplication(), PomodoroService::class.java))
        _timerState.value = TimerState(currentTag = _timerState.value.currentTag)
    }
}
