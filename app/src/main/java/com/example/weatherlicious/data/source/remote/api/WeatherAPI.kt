package com.example.weatherlicious.data.source.remote.api

import com.example.weatherlicious.BuildConfig
import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import retrofit2.Response
import retrofit2.http.GET

interface WeatherAPI {

    @GET("current.json?key=${BuildConfig.API_KEY}&q=London&aqi=no")
    suspend fun getCurrentWeatherByCity(): CurrentWeather

}