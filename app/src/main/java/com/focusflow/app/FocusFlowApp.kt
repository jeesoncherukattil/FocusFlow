package com.focusflow.app

import android.app.Application
import androidx.room.Room
import com.focusflow.app.data.FocusFlowDatabase

class FocusFlowApp : Application() {
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            FocusFlowDatabase::class.java,
            "focusflow.db"
        ).build()
    }
}
