package com.example.weatherlicious.data.source.repository

import com.example.weatherlicious.data.source.local.WeatherLocal
import com.example.weatherlicious.data.source.remote.WeatherRemote
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherLocal: WeatherLocal,
    private val weatherRemote: WeatherRemote,
): WeatherRepository {

    override suspend fun getCurrentWeather() {}

    override suspend fun getWeatherForecastHourly() {}

    override suspend fun getWeatherForecastDaily() {}
}