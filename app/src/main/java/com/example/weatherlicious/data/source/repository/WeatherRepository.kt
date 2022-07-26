package com.example.weatherlicious.data.source.repository

import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import retrofit2.Response

interface WeatherRepository {

    suspend fun getCurrentWeather(): Response<CurrentWeather>

    suspend fun getWeatherForecastHourly(): Response<ForecastWeather>

    suspend fun getWeatherForecastDaily(): Response<ForecastWeather>
}