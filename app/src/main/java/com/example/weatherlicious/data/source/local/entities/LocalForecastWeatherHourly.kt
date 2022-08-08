package com.example.weatherlicious.data.source.local.entities

import android.graphics.Bitmap
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_weather_hourly_table")
data class LocalForecastWeatherHourly(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val hour: String,
    val icon: Bitmap,
    val temperature: String,
    val chanceOfRain: String
)
