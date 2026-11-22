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

    @Query("SELECT SUM(focusMinutes) FROM pomodoro_sessions WHERE completed = 1 AND startTime >= :startOfDay")
    fun getTodayFocusMinutes(startOfDay: Long): Flow<Int?>
}

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: ActivityTag)

    @Query("SELECT * FROM activity_tags")
    fun getAllTags(): Flow<List<ActivityTag>>
}

@Dao
interface BlockedAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: BlockedApp)

    @Query("SELECT packageName FROM blocked_apps WHERE enabled = 1")
    fun getEnabledPackages(): Flow<List<String>>

    @Query("DELETE FROM blocked_apps WHERE packageName = :pkg")
    suspend fun remove(pkg: String)
}
