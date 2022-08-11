package com.example.weatherlicious.data.source.remote

import com.example.weatherlicious.data.model.currentweather.RemoteCurrentWeather
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import retrofit2.Response

interface WeatherRemote {

    suspend fun getCurrentWeather(): Response<RemoteCurrentWeather>

    suspend fun getWeatherForecastHourly(): Response<RemoteForecastWeather>

    suspend fun getWeatherForecastDaily(): Response<RemoteForecastWeather>

    suspend fun searchForCity(name: String): Response<List<CityItem>>
}