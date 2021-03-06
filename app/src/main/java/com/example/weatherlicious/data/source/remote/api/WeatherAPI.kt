package com.example.weatherlicious.data.source.remote.api

import com.example.weatherlicious.BuildConfig
import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import retrofit2.Response
import retrofit2.http.GET

interface WeatherAPI {

    @GET("current.json?key=${BuildConfig.API_KEY}&q=Leipzig&aqi=no")
    suspend fun getCurrentWeatherByCity(): Response<CurrentWeather>

    @GET("forecast.json?key=${BuildConfig.API_KEY}&q=Leipzig&days=10&aqi=no&alerts=no")
    suspend fun getForecastWeatherByCityNextTenDays(): Response<ForecastWeather>

}