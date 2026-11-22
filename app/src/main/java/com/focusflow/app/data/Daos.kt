package com.focusflow.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: PomodoroSession): Long

    @Query("SELECT * FROM pomodoro_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<PomodoroSession>>

    @Query("SELECT SUM(focusMinutes) FROM pomodoro_sessions WHERE completed = 1 AND date(startTime/1000, 'unixepoch') = date('now')")
    fun getTodayFocusMinutes(): Flow<Int?>
}

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: ActivityTag)

    @Query("SELECT * FROM activity_tags")
    fun getAllTags(): Flow<List<ActivityTag>>
}
