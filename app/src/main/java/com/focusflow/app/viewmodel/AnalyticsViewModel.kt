package com.focusflow.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.focusflow.app.FocusFlowApp
import com.focusflow.app.data.PomodoroSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = (application as FocusFlowApp).database.sessionDao()

    val sessions: Flow<List<PomodoroSession>> = dao.getAllSessions()

    private val startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val todayFocusMinutes: Flow<Int?> = dao.getTodayFocusMinutes(startOfDay)
}
