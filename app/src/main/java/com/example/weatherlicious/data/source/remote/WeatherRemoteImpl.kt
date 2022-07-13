package com.example.weatherlicious.data.source.remote

import com.example.weatherlicious.data.source.remote.api.WeatherAPI
import javax.inject.Inject

class WeatherRemoteImpl @Inject constructor(
    private val weatherAPI: WeatherAPI
): WeatherRemote {

    override suspend fun getCurrentWeather() {}

    override suspend fun getWeatherForecastHourly() {}

    override suspend fun getWeatherForecastDaily() {}
}