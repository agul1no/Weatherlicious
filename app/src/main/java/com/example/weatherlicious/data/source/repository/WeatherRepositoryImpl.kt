package com.example.weatherlicious.data.source.repository

import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.source.local.WeatherLocal
import com.example.weatherlicious.data.source.remote.WeatherRemote
import retrofit2.Response
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherLocal: WeatherLocal,
    private val weatherRemote: WeatherRemote,
): WeatherRepository {

    override suspend fun getCurrentWeather(): Response<CurrentWeather> {
        return weatherRemote.getCurrentWeather()
    }

    override suspend fun getWeatherForecastHourly() {}

    override suspend fun getWeatherForecastDaily() {}
}