package com.example.weatherlicious.data.source.remote.api

import com.example.weatherlicious.BuildConfig
import com.example.weatherlicious.data.model.currentweather.RemoteCurrentWeather
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("current.json?key=${BuildConfig.API_KEY}&q=Leipzig&aqi=no")
    suspend fun getCurrentWeatherByCity(): Response<RemoteCurrentWeather>

    @GET("forecast.json?key=${BuildConfig.API_KEY}&q=Leipzig&days=10&aqi=no&alerts=no")
    suspend fun getForecastWeatherByCityNextTenDays(): Response<RemoteForecastWeather>

    @GET("search.json?key=${BuildConfig.API_KEY}")
    suspend fun searchCity(
        @Query("q")
        name: String
    ): Response<List<CityItem>>

}