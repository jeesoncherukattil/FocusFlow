package com.focusflow.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PomodoroSession::class, ActivityTag::class, BlockedApp::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusFlowDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun tagDao(): TagDao
    abstract fun blockedAppDao(): BlockedAppDao
}
