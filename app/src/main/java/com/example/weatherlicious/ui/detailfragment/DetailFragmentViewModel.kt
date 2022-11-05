package com.example.weatherlicious.ui.detailfragment

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
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
class DetailFragmentViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    private var _remoteForecastWeatherByCity = MutableLiveData<Resource<RemoteForecastWeather>>()
    val remoteForecastWeatherByCity = _remoteForecastWeatherByCity

    fun getRemoteForecastWeatherByCity(cityName: String){
        viewModelScope.launch (Dispatchers.IO) {
            try{
                _remoteForecastWeatherByCity.postValue(Resource.Loading())
                val response = weatherRepository.getForecastWeatherByCityNextSevenDays(cityName)
                _remoteForecastWeatherByCity.postValue(handleRemoteWeatherForecastResponse(response))
            }catch (socketTimeoutException: SocketTimeoutException){

            }catch (ioException: IOException){
                Log.d("ioException getRemoteForecastWeatherByCity", ioException.toString())
            }
        }
    }

    private fun handleRemoteWeatherForecastResponse(response: Response<RemoteForecastWeather>): Resource<RemoteForecastWeather>{
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error("Error receiving the Remote Forecast Weather")
    }
}