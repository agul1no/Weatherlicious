package com.example.weatherlicious.data.source.local.entities

import android.graphics.Bitmap
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather_table")
data class LocalCurrentWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val cityName: String,
    val date: String,
    val icon: Bitmap,
    val temperature: String,
    val condition: String,
    val wind: String,
    val maxAndMinTemp: String,
    val feelsLike: String
)
