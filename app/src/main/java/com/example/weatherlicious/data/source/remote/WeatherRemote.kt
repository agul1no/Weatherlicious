package com.example.weatherlicious.data.source.remote

import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import retrofit2.Response

interface WeatherRemote {

    suspend fun getCurrentWeather(): Response<CurrentWeather>

    suspend fun getWeatherForecastHourly()

    suspend fun getWeatherForecastDaily()
}