package com.example.sundayandroidapp.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationService(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    suspend fun getLastKnownLocation(): Location? {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            // If permissions are not granted, return a default location or null
            // For now, returning a default location (Santiago, Chile)
            val defaultLocation = Location("defaultProvider").apply {
                latitude = -33.4489 // Santiago latitude
                longitude = -70.6693 // Santiago longitude
            }
            return defaultLocation
        }

        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnCompleteListener {
                if (it.isSuccessful) {
                    continuation.resume(it.result)
                } else {
                    continuation.resumeWithException(it.exception ?: Exception("Unknown location error"))
                }
            }
        }
    }
}
