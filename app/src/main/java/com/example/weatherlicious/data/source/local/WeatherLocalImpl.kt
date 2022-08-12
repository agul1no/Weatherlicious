package com.example.weatherlicious.data.source.local

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherlicious.data.source.local.entities.*
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

    override suspend fun getLocalCurrentWeather(): LocalCurrentWeather {
        return weatherDatabase.weatherDao.getLocalCurrentWeather()
    }

    override suspend fun insertLocalForecastWeatherHourly(localForecastWeatherHourly: LocalForecastWeatherHourly) {
        weatherDatabase.weatherDao.insertLocalForecastWeatherHourly(localForecastWeatherHourly)
    }

    override suspend fun deleteLocalForecastWeatherHourly() {
        weatherDatabase.weatherDao.deleteLocalForecastWeatherHourly()
    }

    override fun getLocalForecastWeatherHourly(): List<LocalForecastWeatherHourly> {
        return weatherDatabase.weatherDao.getLocalForecastWeatherHourly()
    }

    override suspend fun insertLocalForecastWeatherDaily(localForecastWeatherDaily: LocalForecastWeatherDaily) {
        weatherDatabase.weatherDao.insertLocalForecastWeatherDaily(localForecastWeatherDaily)
    }

    override suspend fun deleteLocalForecastWeatherDaily(){
        weatherDatabase.weatherDao.deleteLocalForecastWeatherDaily()
    }

    override fun getLocalForecastWeatherDaily(): List<LocalForecastWeatherDaily>{
        return weatherDatabase.weatherDao.getLocalForecastWeatherDaily()
    }

    override suspend fun insertLocalCurrentWeatherExtraData(localCurrentWeatherExtraData: LocalCurrentWeatherExtraData){
        weatherDatabase.weatherDao.insertLocalCurrentWeatherExtraData(localCurrentWeatherExtraData)
    }

    override suspend fun deleteCurrentWeatherExtraData(){
        weatherDatabase.weatherDao.deleteCurrentWeatherExtraData()
    }

    override fun getLocalCurrentWeatherExtraData(): LocalCurrentWeatherExtraData{
        return weatherDatabase.weatherDao.getLocalCurrentWeatherExtraData()
    }

    override suspend fun insertCity(city: City) {
        weatherDatabase.weatherDao.insertCity(city)
    }

    override suspend fun deleteCity(city: City) {
        weatherDatabase.weatherDao.deleteCity(city)
    }

    override suspend fun changeMainLocationFromDBToZero() {
        weatherDatabase.weatherDao.changeMainLocationFromDBToZero()
    }

    override fun getPreferredLocation(): List<City> {
        return weatherDatabase.weatherDao.getPreferredLocation()
    }
}