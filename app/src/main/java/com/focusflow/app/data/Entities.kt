package com.focusflow.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityTag: String,
    val focusMinutes: Int,
    val breakMinutes: Int,
    val completed: Boolean,
    val startTime: Instant,
    val endTime: Instant?
)

@Entity(tableName = "activity_tags")
data class ActivityTag(
    @PrimaryKey val name: String,
    val color: String = "#6750A4"
)
