package com.example.weatherlicious.data.source.repository

import com.example.weatherlicious.data.source.local.WeatherLocal
import com.example.weatherlicious.data.source.remote.WeatherRemote
import javax.inject.Inject

interface WeatherRepository {

    suspend fun getCurrentWeather()

    suspend fun getWeatherForecastHourly()

    suspend fun getWeatherForecastDaily()
}