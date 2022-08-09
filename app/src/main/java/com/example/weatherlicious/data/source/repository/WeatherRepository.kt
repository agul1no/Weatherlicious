package com.example.weatherlicious.data.source.repository

import com.example.weatherlicious.data.model.currentweather.RemoteCurrentWeather
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherDaily
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherHourly
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WeatherRepository {

    suspend fun getRemoteCurrentWeather(): Response<RemoteCurrentWeather>

    suspend fun getRemoteWeatherForecastHourly(): Response<RemoteForecastWeather>

    suspend fun getRemoteWeatherForecastDaily(): Response<RemoteForecastWeather>

    suspend fun insertCurrentWeather(localCurrentWeather: LocalCurrentWeather)

    suspend fun deleteCurrentWeather()

    suspend fun getLocalCurrentWeather(): LocalCurrentWeather

    suspend fun insertLocalForecastWeatherHourly(localForecastWeatherHourly: LocalForecastWeatherHourly)

    suspend fun deleteLocalForecastWeatherHourly()

    fun getLocalForecastWeatherHourly(): List<LocalForecastWeatherHourly>

    suspend fun insertLocalForecastWeatherDaily(localForecastWeatherDaily: LocalForecastWeatherDaily)

    suspend fun deleteLocalForecastWeatherDaily()

    fun getLocalForecastWeatherDaily(): List<LocalForecastWeatherDaily>

    suspend fun insertCity(city: City)

    suspend fun deleteCity(city: City)

    fun getPreferredLocation(): Flow<List<City>>
}