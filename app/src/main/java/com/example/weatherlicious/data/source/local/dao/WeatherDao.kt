package com.example.weatherlicious.data.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherlicious.data.source.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(localCurrentWeather: LocalCurrentWeather)

    @Query("DELETE FROM current_weather_table")
    suspend fun deleteCurrentWeather()

    @Query("SELECT * FROM current_weather_table")
    suspend fun getLocalCurrentWeather(): LocalCurrentWeather

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalForecastWeatherHourly(localForecastWeatherHourly: LocalForecastWeatherHourly)

    @Query("DELETE FROM forecast_weather_hourly_table")
    suspend fun deleteLocalForecastWeatherHourly()

    @Query("SELECT * FROM forecast_weather_hourly_table ORDER BY id ASC")
    fun getLocalForecastWeatherHourly(): List<LocalForecastWeatherHourly>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalForecastWeatherDaily(localForecastWeatherDaily: LocalForecastWeatherDaily)

    @Query("DELETE FROM forecast_weather_daily_table")
    suspend fun deleteLocalForecastWeatherDaily()

    @Query("SELECT * FROM forecast_weather_daily_table ORDER BY id ASC")
    fun getLocalForecastWeatherDaily(): List<LocalForecastWeatherDaily>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalCurrentWeatherExtraData(localCurrentWeatherExtraData: LocalCurrentWeatherExtraData)

    @Query("DELETE FROM current_weather_extra_data")
    suspend fun deleteCurrentWeatherExtraData()

    @Query("SELECT * FROM current_weather_extra_data")
    fun getLocalCurrentWeatherExtraData(): LocalCurrentWeatherExtraData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: City)

    @Delete
    suspend fun deleteCity(city: City)

    @Query("SELECT * FROM city_table WHERE preferred_location LIKE 1")
    fun getPreferredLocation(): Flow<List<City>>
}