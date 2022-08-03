package com.example.weatherlicious.data.source.local.dao

import androidx.room.*
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherHourly
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(localCurrentWeather: LocalCurrentWeather)

    @Query("DELETE FROM current_weather_table")
    suspend fun deleteCurrentWeather()

    @Query("SELECT * FROM current_weather_table")
    fun getLocalCurrentWeather(): Flow<LocalCurrentWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecastWeather(vararg localForecastWeather: LocalForecastWeatherHourly)

    @Query("DELETE FROM forecast_weather_hourly_table")
    suspend fun deleteForecastWeather()

    @Query("SELECT * FROM forecast_weather_hourly_table")
    fun getLocalForecastWeather(): Flow<LocalForecastWeatherHourly>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City)

    @Delete
    suspend fun deleteCity(city: City)

    @Query("SELECT * FROM city_table WHERE preferred_location LIKE 1")
    fun getPreferredLocation(): Flow<List<City>>
}