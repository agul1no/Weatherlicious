package com.example.weatherlicious.data.source.local

import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherHourly
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherLocalImpl @Inject constructor(
    private val weatherDatabase: WeatherDatabase
): WeatherLocal {
    override suspend fun insertCurrentWeather(localCurrentWeather: LocalCurrentWeather) {
        weatherDatabase.weatherDao.insertCurrentWeather(localCurrentWeather)
    }

    override suspend fun deleteCurrentWeather() {
        weatherDatabase.weatherDao.deleteCurrentWeather()
    }

    override fun getLocalCurrentWeather(): Flow<LocalCurrentWeather> {
        return weatherDatabase.weatherDao.getLocalCurrentWeather()
    }

    override suspend fun insertForecastWeather(vararg localForecastWeather: LocalForecastWeatherHourly) {
        weatherDatabase.weatherDao.insertForecastWeather()
    }

    override suspend fun deleteForecastWeather() {
        weatherDatabase.weatherDao.deleteForecastWeather()
    }

    override fun getLocalForecastWeather(): Flow<LocalForecastWeatherHourly> {
        return weatherDatabase.weatherDao.getLocalForecastWeather()
    }

    override suspend fun insertCity(city: City) {
        weatherDatabase.weatherDao.insertCity(city)
    }

    override suspend fun deleteCity(city: City) {
        weatherDatabase.weatherDao.deleteCity(city)
    }

    override fun getPreferredLocation(): Flow<List<City>> {
        return weatherDatabase.weatherDao.getPreferredLocation()
    }
}