package com.example.weatherlicious

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.data.source.repository.WeatherRepository
import com.example.weatherlicious.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private var _remoteForecastWeatherByCity = MutableLiveData<Resource<RemoteForecastWeather>>()
    val remoteForecastWeatherByCity = _remoteForecastWeatherByCity

    private var _listOfResponses = MutableLiveData<MutableList<Response<RemoteForecastWeather>>>()
    val listOfResponses = _listOfResponses

    private val TAG = "Main Activity View Model"

    fun getRemoteForecastWeatherByCity() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i(TAG, "Start getRemoteForecastWeatherByCity()")
            try {
                _remoteForecastWeatherByCity.postValue(Resource.Loading())
                //_mainLocation.postValue(weatherRepository.getMainLocation().name)
                var mainLocation = weatherRepository.getMainLocation()?.name
                if (mainLocation == null) {
                    mainLocation = "Leipzig"
                }
                val response = weatherRepository.getForecastWeatherByCityNextSevenDays(mainLocation)
                _remoteForecastWeatherByCity.postValue(handleRemoteWeatherForecastResponse(response))
            } catch (socketTimeoutException: SocketTimeoutException) {
                Log.d(
                    "socketTimeoutException getRemoteForecastWeatherByCity",
                    socketTimeoutException.toString()
                )
            } catch (ioException: IOException) {
                //Toast.makeText(applicationContext, "$ioException", Toast.LENGTH_SHORT).show()
                Log.d("ioException getRemoteForecastWeatherByCity", ioException.toString())
            }
            Log.i(TAG, "End getRemoteForecastWeatherByCity()")
        }
    }

    fun getListOfRemoteForecastWeathersByCity(listOfLocations: List<City>) {
        val listOfResponses = mutableListOf<Response<RemoteForecastWeather>>()
        viewModelScope.launch(Dispatchers.IO) {
            listOfLocations.forEach { location ->
                val response =
                    weatherRepository.getForecastWeatherByCityNextSevenDays(location.name)
                listOfResponses.add(response)
            }
            _listOfResponses.postValue(listOfResponses)
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

    fun insertCityResettingMainLocation(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.changeMainLocationFromDBToZero()
            weatherRepository.insertCity(city)
        }
    }

    fun searchCityObjectInDB(name: String): City {
        return weatherRepository.searchCityObjectInDB(name)
    }


    fun getMainLocationLive(): LiveData<City> {
        return weatherRepository.getMainLocationLive()
    }

    fun getNotMainLocations(): LiveData<List<City>> {
        return weatherRepository.getLocationsList()
    }

    fun deleteCity(city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteCity(city)
        }
    }
}