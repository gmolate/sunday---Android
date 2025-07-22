package com.example.sunday.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sunday.data.repository.Repository
import com.example.sunday.domain.HealthConnectManager
import com.example.sunday.domain.LocationManager
import com.example.sunday.domain.VitaminDCalculator

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                Repository(),
                LocationManager(context),
                VitaminDCalculator(),
                HealthConnectManager(context),
                context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
