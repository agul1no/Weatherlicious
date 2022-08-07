package com.example.weatherlicious.data.source.repository

import androidx.lifecycle.LiveData
import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherHourly
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WeatherRepository {

    suspend fun getRemoteCurrentWeather(): Response<CurrentWeather>

    suspend fun getRemoteWeatherForecastHourly(): Response<ForecastWeather>

    suspend fun getRemoteWeatherForecastDaily(): Response<ForecastWeather>

    suspend fun insertCurrentWeather(localCurrentWeather: LocalCurrentWeather)

    suspend fun deleteCurrentWeather()

    suspend fun getLocalCurrentWeather(): LocalCurrentWeather

    suspend fun insertLocalForecastWeatherHourly(localForecastWeatherHourly: LocalForecastWeatherHourly)

    suspend fun deleteLocalForecastWeatherHourly()

    fun getLocalForecastWeatherHourly(): List<LocalForecastWeatherHourly>

    suspend fun insertCity(city: City)

    suspend fun deleteCity(city: City)

    fun getPreferredLocation(): Flow<List<City>>
}