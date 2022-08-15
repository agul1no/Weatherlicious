package com.example.weatherlicious.data.source.remote

import com.example.weatherlicious.data.model.currentweather.RemoteCurrentWeather
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import com.example.weatherlicious.data.source.remote.api.WeatherAPI
import retrofit2.Response
import javax.inject.Inject

class WeatherRemoteImpl @Inject constructor(
    private val weatherAPI: WeatherAPI
): WeatherRemote {

    override suspend fun getCurrentWeather(): Response<RemoteCurrentWeather> {
        return weatherAPI.getCurrentWeatherByCity()
    }

    override suspend fun getWeatherForecastHourly(): Response<RemoteForecastWeather> {
        return weatherAPI.getForecastWeatherByCityNextTenDays()
    }

    override suspend fun getWeatherForecastDaily(): Response<RemoteForecastWeather> {
        return weatherAPI.getForecastWeatherByCityNextTenDays()
    }

    override suspend fun getForecastWeatherByCityNextSevenDays(mainLocation: String): Response<RemoteForecastWeather> {
        return weatherAPI.getForecastWeatherByCityNextSevenDays(mainLocation)
    }

    override suspend fun searchForCity(name: String): Response<List<CityItem>> {
        return weatherAPI.searchCity(name)
    }
}