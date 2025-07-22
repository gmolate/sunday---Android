package com.example.sunday.domain

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.VitaminDRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.InternationalUnits
import java.time.Instant
import java.time.ZonedDateTime

class HealthConnectManager(private val context: Context) {

    private val healthConnectClient: HealthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    suspend fun checkAvailability(): Int {
        return HealthConnectClient.sdkStatus(context)
    }

    suspend fun requestPermissions(permissions: Set<HealthPermission>) {
        val request = androidx.health.connect.client.permission.PermissionRequest.create(permissions)
        healthConnectClient.permissionController.requestPermissions(request)
    }

    suspend fun writeVitaminD(amount: Double, startTime: ZonedDateTime, endTime: ZonedDateTime) {
        val records = listOf(
            VitaminDRecord(
                amount = InternationalUnits(amount),
                startTime = startTime.toInstant(),
                endTime = endTime.toInstant(),
                startZoneOffset = startTime.offset,
                endZoneOffset = endTime.offset
            )
        )
        healthConnectClient.insertRecords(records)
    }

    suspend fun readVitaminDHistory(startTime: Instant, endTime: Instant): List<VitaminDRecord> {
        val request = ReadRecordsRequest(
            recordType = VitaminDRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }
}
