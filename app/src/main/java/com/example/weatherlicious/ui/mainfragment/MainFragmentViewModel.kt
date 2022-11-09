package com.example.weatherlicious.ui.mainfragment

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.weatherlicious.data.model.currentweather.RemoteCurrentWeather
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.source.local.entities.*
import com.example.weatherlicious.data.source.repository.WeatherRepository
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter.Companion.categorizeUVValue
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateDayMonthYearHourMin
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateYearMonthDay
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDayOfTheWeek
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHour
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHourMin
import com.example.weatherlicious.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val applicationContext: Application,
    private val weatherRepository: WeatherRepository
) : AndroidViewModel(applicationContext) {

    private var _remoteCurrentWeather = MutableLiveData<Response<RemoteCurrentWeather>>()
    val remoteCurrentWeather = _remoteCurrentWeather

    private var _remoteForecastWeatherHourly = MutableLiveData<Resource<RemoteForecastWeather>>()
    val remoteForecastWeatherHourly = _remoteForecastWeatherHourly

    private var _remoteForecastWeatherDaily = MutableLiveData<Resource<RemoteForecastWeather>>()
    val remoteForecastWeatherDaily = _remoteForecastWeatherDaily

    private var _remoteForecastWeatherByCity = MutableLiveData<Resource<RemoteForecastWeather>>()
    val remoteForecastWeatherByCity = _remoteForecastWeatherByCity

    private var _localCurrentWeather = MutableLiveData<Resource<LocalCurrentWeather>>()
    val localCurrentWeather = _localCurrentWeather

    private var _localForecastWeatherHourly = MutableLiveData<List<LocalForecastWeatherHourly>>()
    val localForecastWeatherHourly = _localForecastWeatherHourly

    private var _localForecastWeatherDaily = MutableLiveData<List<LocalForecastWeatherDaily>>()
    val localForecastWeatherDaily = _localForecastWeatherDaily

    private var _localCurrentWeatherExtraData = MutableLiveData<LocalCurrentWeatherExtraData>()
    val localCurrentWeatherExtraData = _localCurrentWeatherExtraData

    private var _mainLocation = MutableLiveData<City>()
    val mainLocation = _mainLocation

    fun getMainLocation(): City? {
        return weatherRepository.getMainLocation()
    }

    fun getMainLocationLive(): LiveData<City> {
        return weatherRepository.getMainLocationLive()
    }

    fun getOtherLocations(): LiveData<List<City>> {
        return weatherRepository.getLocationsList()
    }

    /** transforming remote data into local data functions **/

    fun transformRemoteForecastWeatherIntoLocalCurrentWeather(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteCurrentWeather()
            var mainLocation = weatherRepository.getMainLocation()?.name
            if (mainLocation == null) {
                mainLocation = "Leipzig"
            }
            val response = weatherRepository.getForecastWeatherByCityNextSevenDays(mainLocation)
            val localCurrentWeather = LocalCurrentWeather(
                0,
                response.body()!!.location.name,
                response.body()!!.location.localtime.dateYearMonthDayHourMinToMillis()
                    .millisToDateDayMonthYearHourMin(),
                convertUrlIconToBitmap(
                    "https:${response.body()!!.current.condition.icon}",
                    context
                ),
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
            var mainLocation = weatherRepository.getMainLocation()?.name
            if (mainLocation == null) {
                mainLocation = "Leipzig"
            }
            val response = weatherRepository.getForecastWeatherByCityNextSevenDays(mainLocation)
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

    private fun insertLocalForecastWeatherHourly(
        id: Int,
        response: Response<RemoteForecastWeather>,
        firstObject: Int,
        timeCounter: Int,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val localForecastWeatherHourly = LocalForecastWeatherHourly(
                id,
                response.body()!!.forecast.forecastday[firstObject].hour[timeCounter].time.dateYearMonthDayHourMinToMillis()
                    .millisToHourMin(),
                convertUrlIconToBitmap(
                    "https:${response.body()!!.forecast.forecastday[firstObject].hour[timeCounter].condition.icon}",
                    context
                ),
                "${response.body()!!.forecast.forecastday[firstObject].hour[timeCounter].temp_c.toInt()}°",
                "${response.body()!!.forecast.forecastday[firstObject].hour[timeCounter].chance_of_rain} %"
            )
            weatherRepository.insertLocalForecastWeatherHourly(localForecastWeatherHourly)
        }
    }

    fun transformRemoteForecastWeatherDailyIntoLocalForecastWeatherDaily(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteLocalForecastWeatherDaily()
            var mainLocation = weatherRepository.getMainLocation()?.name
            if (mainLocation == null) {
                mainLocation = "Leipzig"
            }
            val response = weatherRepository.getForecastWeatherByCityNextSevenDays(mainLocation)
            val sizeListOfDays = response.body()!!.forecast.forecastday.size
            var firstObject = 0
            for (i in 1..sizeListOfDays) {
                val localForecastWeatherDaily = LocalForecastWeatherDaily(
                    i,
                    saveDayOfTheWeek(response.body()!!.forecast.forecastday[firstObject].date),
                    "${response.body()!!.forecast.forecastday[firstObject].day.daily_chance_of_rain} %",
                    convertUrlIconToBitmap(
                        "https:${response.body()!!.forecast.forecastday[firstObject].day.condition.icon}",
                        context
                    ),
                    convertUrlIconToBitmap(
                        "https:${response.body()!!.forecast.forecastday[firstObject].hour[22].condition.icon}",
                        context
                    ),
                    "${response.body()!!.forecast.forecastday[firstObject].day.maxtemp_c.toInt()}° / ${response.body()!!.forecast.forecastday[firstObject].day.mintemp_c.toInt()}°"
                )
                weatherRepository.insertLocalForecastWeatherDaily(localForecastWeatherDaily)
                firstObject++
            }
        }
    }

    private fun saveDayOfTheWeek(dayOfTheWeek: String): String {
        return if (dayOfTheWeek == getDay()) {
            "Today"
        } else {
            dayOfTheWeek.dateYearMonthDayToMillis().millisToDayOfTheWeek()
        }
    }

    private fun getDay(): String {
        return Calendar.getInstance().timeInMillis.millisToDateYearMonthDay()
    }

    fun transformRemoteCurrentWeatherExtraDataIntoLocalCurrentWeatherExtraData() {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteCurrentWeatherExtraData()
            var mainLocation = weatherRepository.getMainLocation()?.name
            if (mainLocation == null) {
                mainLocation = "Leipzig"
            }
            val response = weatherRepository.getForecastWeatherByCityNextSevenDays(mainLocation)
            val localCurrentWeatherExtraData = LocalCurrentWeatherExtraData(
                0,
                categorizeUVValue(response.body()!!.forecast.forecastday[0].day.uv),
                response.body()!!.forecast.forecastday[0].astro.sunrise,
                response.body()!!.forecast.forecastday[0].astro.sunset,
                CurrentWeatherExtraDataFormatter.transformWindDirectionResponse(response.body()!!.current.wind_dir),
                "${response.body()!!.current.humidity} %"
            )
            weatherRepository.insertLocalCurrentWeatherExtraData(localCurrentWeatherExtraData)
        }
    }

    /** Remote Weather functions **/

    private fun getRemoteCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _remoteCurrentWeather.postValue(weatherRepository.getRemoteCurrentWeather())
            } catch (socketTimeoutException: SocketTimeoutException) {

            } catch (ioException: IOException) {

            }
        }
    }

    fun getRemoteForecastWeatherHourly() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _remoteForecastWeatherHourly.postValue(Resource.Loading())
                val response = weatherRepository.getRemoteWeatherForecastHourly()
                _remoteForecastWeatherHourly.postValue(handleRemoteWeatherForecastResponse(response))
            } catch (socketTimeoutException: SocketTimeoutException) {
                Toast.makeText(applicationContext, "$socketTimeoutException", Toast.LENGTH_SHORT)
                    .show()
            } catch (ioException: IOException) {
                //Toast.makeText(applicationContext, "$ioException", Toast.LENGTH_SHORT).show()
                Log.d("ioException getRemoteForecastWeatherHourly", ioException.toString())
            }
        }
    }

    private fun handleRemoteWeatherForecastResponse(response: Response<RemoteForecastWeather>): Resource<RemoteForecastWeather> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error("Error receiving the Remote Forecast Weather")
    }

    fun getRemoteForecastWeatherDaily() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _remoteForecastWeatherDaily.postValue(Resource.Loading())
                val response = weatherRepository.getRemoteWeatherForecastDaily()
                _remoteForecastWeatherDaily.postValue(handleRemoteWeatherForecastResponse(response))
            } catch (socketTimeoutException: SocketTimeoutException) {

            } catch (ioException: IOException) {

            }
        }
    }

    fun getRemoteForecastWeatherByCity() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _remoteForecastWeatherByCity.postValue(Resource.Loading())
                _mainLocation.postValue(weatherRepository.getMainLocation())
                var mainLocation = weatherRepository.getMainLocation()?.name
                if (mainLocation == null) {
                    mainLocation = "Leipzig"
                }
                val response = weatherRepository.getForecastWeatherByCityNextSevenDays(mainLocation)
                _remoteForecastWeatherByCity.postValue(handleRemoteWeatherForecastResponse(response))
            } catch (socketTimeoutException: SocketTimeoutException) {
                Toast.makeText(applicationContext, "$socketTimeoutException", Toast.LENGTH_SHORT)
                    .show()
            } catch (ioException: IOException) {
                //Toast.makeText(applicationContext, "$ioException", Toast.LENGTH_SHORT).show()
                Log.d("ioException getRemoteForecastWeatherByCity", ioException.toString())
            }
        }
    }

    /** Local Weather functions **/

    fun getLocalCurrentWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _localCurrentWeather.postValue(Resource.Loading())
                val response = weatherRepository.getLocalCurrentWeather()
                _localCurrentWeather.postValue(handleLocalWeatherForecastResponse(response))
            } catch (exception: Exception) {
                Toast.makeText(applicationContext, "$exception", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun handleLocalWeatherForecastResponse(response: LocalCurrentWeather?): Resource<LocalCurrentWeather> {
        response?.let { resultResponse ->
            return Resource.Success(resultResponse)
        }
        return Resource.Error("Local Current Weather is null")
    }

    fun getLocalForecastWeatherHourly() {
        viewModelScope.launch(Dispatchers.IO) {
            _localForecastWeatherHourly.postValue(weatherRepository.getLocalForecastWeatherHourly())
        }
    }

    fun getLocalForecastWeatherDaily() {
        viewModelScope.launch(Dispatchers.IO) {
            _localForecastWeatherDaily.postValue(weatherRepository.getLocalForecastWeatherDaily())
        }
    }

    fun getLocalCurrentWeatherExtraData() {
        viewModelScope.launch(Dispatchers.IO) {
            _localCurrentWeatherExtraData.postValue(weatherRepository.getLocalCurrentWeatherExtraData())
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