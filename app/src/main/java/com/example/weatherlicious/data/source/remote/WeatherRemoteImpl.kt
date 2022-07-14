package com.example.weatherlicious.data.source.remote

import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.source.remote.api.WeatherAPI
import retrofit2.Response
import javax.inject.Inject

class WeatherRemoteImpl @Inject constructor(
    private val weatherAPI: WeatherAPI
): WeatherRemote {

    override suspend fun getCurrentWeather(): CurrentWeather {
        return weatherAPI.getCurrentWeatherByCity()
    }

    override suspend fun getWeatherForecastHourly() {}

    override suspend fun getWeatherForecastDaily() {}
}