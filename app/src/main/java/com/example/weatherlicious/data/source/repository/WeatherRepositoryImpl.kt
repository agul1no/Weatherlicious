package com.example.weatherlicious.data.source.repository

import androidx.lifecycle.LiveData
import com.example.weatherlicious.data.model.currentweather.RemoteCurrentWeather
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import com.example.weatherlicious.data.source.local.WeatherLocal
import com.example.weatherlicious.data.source.local.entities.*
import com.example.weatherlicious.data.source.remote.WeatherRemote
import retrofit2.Response
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherLocal: WeatherLocal,
    private val weatherRemote: WeatherRemote,
): WeatherRepository {

    override suspend fun getRemoteCurrentWeather(): Response<RemoteCurrentWeather> {
        return weatherRemote.getCurrentWeather()
    }

    override suspend fun getRemoteWeatherForecastHourly(): Response<RemoteForecastWeather> {
        return weatherRemote.getWeatherForecastHourly()
    }

    override suspend fun getRemoteWeatherForecastDaily(): Response<RemoteForecastWeather> {
        return weatherRemote.getWeatherForecastDaily()
    }

    override suspend fun getForecastWeatherByCityNextSevenDays(mainLocation: String): Response<RemoteForecastWeather>{
        return weatherRemote.getForecastWeatherByCityNextSevenDays(mainLocation)
    }

    override suspend fun searchForCity(name: String): Response<List<CityItem>> {
        return weatherRemote.searchForCity(name)
    }

    override suspend fun insertCurrentWeather(localCurrentWeather: LocalCurrentWeather) {
        weatherLocal.insertCurrentWeather(localCurrentWeather)
    }

    override suspend fun deleteCurrentWeather() {
        weatherLocal.deleteCurrentWeather()
    }

    override suspend fun getLocalCurrentWeather(): LocalCurrentWeather {
        return weatherLocal.getLocalCurrentWeather()
    }

    override suspend fun insertLocalForecastWeatherHourly(localForecastWeatherHourly: LocalForecastWeatherHourly) {
        weatherLocal.insertLocalForecastWeatherHourly(localForecastWeatherHourly)
    }

    override suspend fun deleteLocalForecastWeatherHourly() {
        weatherLocal.deleteLocalForecastWeatherHourly()
    }

    override fun getLocalForecastWeatherHourly(): List<LocalForecastWeatherHourly> {
        return weatherLocal.getLocalForecastWeatherHourly()
    }

    override suspend fun insertLocalForecastWeatherDaily(localForecastWeatherDaily: LocalForecastWeatherDaily) {
        weatherLocal.insertLocalForecastWeatherDaily(localForecastWeatherDaily)
    }

    override suspend fun deleteLocalForecastWeatherDaily(){
        weatherLocal.deleteLocalForecastWeatherDaily()
    }

    override fun getLocalForecastWeatherDaily(): List<LocalForecastWeatherDaily>{
        return weatherLocal.getLocalForecastWeatherDaily()
    }

    override suspend fun insertLocalCurrentWeatherExtraData(localCurrentWeatherExtraData: LocalCurrentWeatherExtraData){
        weatherLocal.insertLocalCurrentWeatherExtraData(localCurrentWeatherExtraData)
    }

    override suspend fun deleteCurrentWeatherExtraData(){
        weatherLocal.deleteCurrentWeatherExtraData()
    }

    override fun getLocalCurrentWeatherExtraData(): LocalCurrentWeatherExtraData {
        return weatherLocal.getLocalCurrentWeatherExtraData()
    }

    override suspend fun insertCity(city: City) {
        weatherLocal.insertCity(city)
    }

    override suspend fun deleteCity(city: City) {
        weatherLocal.deleteCity(city)
    }

    override suspend fun changeMainLocationFromDBToZero() {
        weatherLocal.changeMainLocationFromDBToZero()
    }

    override fun getMainLocationLive(): LiveData<City> {
        return weatherLocal.getMainLocationLive()
    }

    override fun getMainLocation(): City? {
        return weatherLocal.getMainLocation()
    }

    override fun getLocationsList(): LiveData<List<City>> {
        return weatherLocal.getLocationsList()
    }

    override fun getLocationsListName(): LiveData<List<String>> {
        return weatherLocal.getLocationsListName()
    }

    override fun searchCityObjectInDB(name: String): City{
        return weatherLocal.searchCityObjectInDB(name)
    }
}