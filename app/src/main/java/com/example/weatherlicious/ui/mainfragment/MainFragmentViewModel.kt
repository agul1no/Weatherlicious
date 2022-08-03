package com.example.weatherlicious.ui.mainfragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.data.source.repository.WeatherRepository
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateDayMonthYearHourMin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private var _remoteCurrentWeather = MutableLiveData<Response<CurrentWeather>>()
    val remoteCurrentWeather = _remoteCurrentWeather

    private var _forecastWeatherHourly = MutableLiveData<Response<ForecastWeather>>()
    val forecastWeatherHourly = _forecastWeatherHourly

    private var _forecastWeatherDaily = MutableLiveData<Response<ForecastWeather>>()
    val forecastWeatherDaily = _forecastWeatherDaily

    private var _localCurrentWeather = MutableLiveData<LocalCurrentWeather>()
    val localCurrentWeather = _localCurrentWeather
    
    fun transformRemoteForecastWeatherIntoLocalCurrentWeather(context: Context){
        viewModelScope.launch (Dispatchers.IO) {
            weatherRepository.deleteCurrentWeather()
            val response = weatherRepository.getRemoteWeatherForecastHourly()
            val cityName = response.body()!!.location.name
            val timeStamp = response.body()!!.location.localtime.dateYearMonthDayHourMinToMillis().millisToDateDayMonthYearHourMin()
            val icon = convertUrlIconToBitmap("https:${response.body()!!.current.condition.icon}", context)
            val temperature = "${response.body()!!.current.temp_c.toInt()}°"
            val condition = response.body()!!.current.condition.text
            val wind = "Wind:  ${response.body()!!.current.wind_kph.toInt()} Kph"
            val maxAndMinTemp = "${response.body()!!.forecast.forecastday[0].day.maxtemp_c.toInt()}° " +
                    "/ ${response.body()!!.forecast.forecastday[0].day.mintemp_c.toInt()}°"
            val feelsLike = "Feelslike:  ${response.body()!!.current.feelslike_c.toInt()}°"

            val localCurrentWeather = LocalCurrentWeather(0, cityName, timeStamp, icon , temperature, condition, wind, maxAndMinTemp, feelsLike)
            weatherRepository.insertCurrentWeather(localCurrentWeather)
        }
    }

    private fun getRemoteCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            //_currentWeather.value = weatherRepository.getCurrentWeather().body()
            _remoteCurrentWeather.postValue(weatherRepository.getRemoteCurrentWeather())
        }
    }

    fun getRemoteForecastWeatherHourly() {
        viewModelScope.launch(Dispatchers.IO) {
            _forecastWeatherHourly.postValue(weatherRepository.getRemoteWeatherForecastHourly())
        }
    }

    fun getRemoteForecastWeatherDaily() {
        viewModelScope.launch(Dispatchers.IO) {
            _forecastWeatherDaily.postValue(weatherRepository.getRemoteWeatherForecastDaily())
        }
    }

    fun getLocalCurrentWeather(){
        viewModelScope.launch (Dispatchers.IO) {
            weatherRepository.getLocalCurrentWeather().collect(){ localCurrentWeather ->
                _localCurrentWeather.postValue(localCurrentWeather) // TODO localCurrentWeather is null
            }
        }
    }

    private suspend fun convertUrlIconToBitmap(url: String, context: Context): Bitmap {
        val loading = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

}