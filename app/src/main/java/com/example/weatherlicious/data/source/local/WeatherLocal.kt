package com.example.weatherlicious.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherlicious.data.source.local.entities.*
import kotlinx.coroutines.flow.Flow

interface WeatherLocal {

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

    fun getLocationsListName(): LiveData<List<String>>

    fun searchCityObjectInDB(name: String): City
}