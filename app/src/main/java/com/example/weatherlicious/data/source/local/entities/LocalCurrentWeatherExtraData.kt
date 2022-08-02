package com.example.weatherlicious.data.source.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather_extra_data")
data class LocalCurrentWeatherExtraData(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val uvIndex: String,
    val sunrise: String,
    val sunset: String,
    val windDirection: String,
    val humidity: String
)
