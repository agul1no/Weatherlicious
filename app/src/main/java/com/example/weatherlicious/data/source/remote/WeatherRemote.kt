package com.example.weatherlicious.data.source.remote

interface WeatherRemote {

    suspend fun getCurrentWeather()

    suspend fun getWeatherForecastHourly()

    suspend fun getWeatherForecastDaily()
}