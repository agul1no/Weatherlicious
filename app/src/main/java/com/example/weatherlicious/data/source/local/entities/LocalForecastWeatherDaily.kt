package com.example.weatherlicious.data.source.local.entities

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_weather_daily_table")
data class LocalForecastWeatherDaily(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val dayOfTheWeek: String,
    val chanceOfRain: String,
    val iconDay: Bitmap,
    val iconNight: Bitmap,
    val maxAndMinTemp: String
)
