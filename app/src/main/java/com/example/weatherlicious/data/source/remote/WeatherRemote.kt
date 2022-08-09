package com.example.weatherlicious.data.source.remote

import com.example.weatherlicious.data.model.currentweather.RemoteCurrentWeather
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import retrofit2.Response

interface WeatherRemote {

    suspend fun getCurrentWeather(): Response<RemoteCurrentWeather>

    suspend fun getWeatherForecastHourly(): Response<RemoteForecastWeather>

    suspend fun getWeatherForecastDaily(): Response<RemoteForecastWeather>
}