package com.example.weatherlicious.ui.mainfragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherHourly
import com.example.weatherlicious.data.source.repository.WeatherRepository
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateDayMonthYearHourMin
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHour
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHourMin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*
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

    private var _localForecastWeatherHourly = MutableLiveData<List<LocalForecastWeatherHourly>>()
    val localForecastWeatherHourly = _localForecastWeatherHourly

    fun transformRemoteForecastWeatherIntoLocalCurrentWeather(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteCurrentWeather()
            val response = weatherRepository.getRemoteWeatherForecastHourly()
            val localCurrentWeather = LocalCurrentWeather(
                0,
                response.body()!!.location.name,
                response.body()!!.location.localtime.dateYearMonthDayHourMinToMillis().millisToDateDayMonthYearHourMin(),
                convertUrlIconToBitmap("https:${response.body()!!.current.condition.icon}", context),
                "${response.body()!!.current.temp_c.toInt()}°",
                response.body()!!.current.condition.text,
                "Wind:  ${response.body()!!.current.wind_kph.toInt()} Kph",
                "${response.body()!!.forecast.forecastday[0].day.maxtemp_c.toInt()}° " +
                        "/ ${response.body()!!.forecast.forecastday[0].day.mintemp_c.toInt()}°",
                "Feelslike:  ${response.body()!!.current.feelslike_c.toInt()}°"
            )
            weatherRepository.insertCurrentWeather(localCurrentWeather)
        }
    }

    fun transformRemoteForecastWeatherIntoLocalForecastWeatherHourly(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteLocalForecastWeatherHourly()
            val response = weatherRepository.getRemoteWeatherForecastHourly()
            var firstObject = 0
            var timeCounter = Calendar.getInstance().timeInMillis.millisToHour().toInt()
            for (i in 1..24) {
                if (timeCounter == 23) {
                    insertLocalForecastWeatherHourly(i, response, firstObject, timeCounter, context)
                    firstObject = 1
                    timeCounter = 0
                }
                insertLocalForecastWeatherHourly(i, response, firstObject, timeCounter, context)
                timeCounter++
            }
        }
    }

    private fun insertLocalForecastWeatherHourly(id: Int, response: Response<ForecastWeather>, firstObject: Int, timeCounter:Int , context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val localForecastWeatherHourly = LocalForecastWeatherHourly(
                id,
                response.body()!!.forecast.forecastday[firstObject].hour[timeCounter].time.dateYearMonthDayHourMinToMillis()
                    .millisToHourMin(),
                convertUrlIconToBitmap("https:${response.body()!!.forecast.forecastday[firstObject].hour[timeCounter].condition.icon}", context),
                "${response.body()!!.forecast.forecastday[firstObject].hour[timeCounter].temp_c.toInt()}°",
                "${response.body()!!.forecast.forecastday[firstObject].hour[timeCounter].chance_of_rain} %"
            )
            weatherRepository.insertLocalForecastWeatherHourly(localForecastWeatherHourly)
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

    fun getLocalCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            _localCurrentWeather.postValue(weatherRepository.getLocalCurrentWeather())
        }
    }

    fun getLocalForecastWeatherHourly(){
        viewModelScope.launch(Dispatchers.IO) {
            _localForecastWeatherHourly.postValue(weatherRepository.getLocalForecastWeatherHourly())
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