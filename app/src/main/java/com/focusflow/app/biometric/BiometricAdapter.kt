package com.focusflow.app.biometric

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Reads heart rate and stress from Health Connect to adapt focus/break lengths.
 */
class BiometricAdapter(private val client: HealthConnectClient) {

    suspend fun getRecentHeartRate(): Int? {
        return try {
            val request = ReadRecordsRequest(
                recordType = HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(
                    Instant.now().minus(5, ChronoUnit.MINUTES),
                    Instant.now()
                )
            )
            val response = client.readRecords(request)
            response.records.lastOrNull()?.samples?.lastOrNull()?.beatsPerMinute?.toInt()
        } catch (e: Exception) {
            null
        }
    }

    fun adjustIntervals(baseFocus: Int, baseBreak: Int, heartRate: Int?, stress: Float?): Pair<Int, Int> {
        return when {
            heartRate != null && heartRate > 100 -> baseFocus - 5 to baseBreak + 2
            heartRate != null && heartRate < 70 -> baseFocus + 5 to baseBreak - 1
            else -> baseFocus to baseBreak
        }
    }
}
