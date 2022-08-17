package com.example.weatherlicious.data.source.repository

import androidx.lifecycle.LiveData
import com.example.weatherlicious.data.model.currentweather.RemoteCurrentWeather
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import com.example.weatherlicious.data.source.local.entities.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WeatherRepository {

    suspend fun getRemoteCurrentWeather(): Response<RemoteCurrentWeather>

    suspend fun getRemoteWeatherForecastHourly(): Response<RemoteForecastWeather>

    suspend fun getRemoteWeatherForecastDaily(): Response<RemoteForecastWeather>

    suspend fun getForecastWeatherByCityNextSevenDays(mainLocation: String): Response<RemoteForecastWeather>

    suspend fun searchForCity(name: String): Response<List<CityItem>>

    suspend fun insertCurrentWeather(localCurrentWeather: LocalCurrentWeather)

    suspend fun deleteCurrentWeather()

    suspend fun getLocalCurrentWeather(): LocalCurrentWeather

    suspend fun insertLocalForecastWeatherHourly(localForecastWeatherHourly: LocalForecastWeatherHourly)

    suspend fun deleteLocalForecastWeatherHourly()

    fun getLocalForecastWeatherHourly(): List<LocalForecastWeatherHourly>

    suspend fun insertLocalForecastWeatherDaily(localForecastWeatherDaily: LocalForecastWeatherDaily)

    suspend fun deleteLocalForecastWeatherDaily()

    fun getLocalForecastWeatherDaily(): List<LocalForecastWeatherDaily>

    suspend fun insertLocalCurrentWeatherExtraData(localCurrentWeatherExtraData: LocalCurrentWeatherExtraData)

    suspend fun deleteCurrentWeatherExtraData()

    fun getLocalCurrentWeatherExtraData(): LocalCurrentWeatherExtraData

    suspend fun insertCity(city: City)

    suspend fun deleteCity(city: City)

    suspend fun changeMainLocationFromDBToZero()

    fun getMainLocationLive(): LiveData<City>

    fun getMainLocation(): City?

    fun getLocationsList(): LiveData<List<City>>
}