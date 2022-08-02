package com.example.weatherlicious.data.source.repository

import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.source.local.WeatherLocal
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherHourly
import com.example.weatherlicious.data.source.remote.WeatherRemote
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherLocal: WeatherLocal,
    private val weatherRemote: WeatherRemote,
): WeatherRepository {

    override suspend fun getRemoteCurrentWeather(): Response<CurrentWeather> {
        return weatherRemote.getCurrentWeather()
    }

    override suspend fun getRemoteWeatherForecastHourly(): Response<ForecastWeather> {
        return weatherRemote.getWeatherForecastHourly()
    }

    override suspend fun getRemoteWeatherForecastDaily(): Response<ForecastWeather> {
        return weatherRemote.getWeatherForecastDaily()
    }

    override suspend fun insertCurrentWeather(localCurrentWeather: LocalCurrentWeather) {
        weatherLocal.insertCurrentWeather(localCurrentWeather)
    }

    override suspend fun deleteCurrentWeather() {
        weatherLocal.deleteCurrentWeather()
    }

    override fun getLocalCurrentWeather(): Flow<LocalCurrentWeather> {
        return weatherLocal.getLocalCurrentWeather()
    }

    override suspend fun insertForecastWeather(vararg localForecastWeather: LocalForecastWeatherHourly) {
        weatherLocal.insertForecastWeather()
    }

    override suspend fun deleteForecastWeather() {
        weatherLocal.deleteForecastWeather()
    }

    override fun getLocalForecastWeather(): Flow<LocalForecastWeatherHourly> {
        return weatherLocal.getLocalForecastWeather()
    }

    override suspend fun insertCity(city: City) {
        weatherLocal.insertCity(city)
    }

    override suspend fun deleteCity(city: City) {
        weatherLocal.deleteCity(city)
    }

    override fun getPreferredLocation(): Flow<List<City>> {
        return weatherLocal.getPreferredLocation()
    }
}