package com.example.weatherlicious.ui.mainfragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.source.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    //private var _currentWeather = MutableLiveData<Response<CurrentWeather>>()
    //val currentWeather = _currentWeather

    private var _forecastWeatherHourly = MutableLiveData<Response<ForecastWeather>>()
    val forecastWeatherHourly = _forecastWeatherHourly

    private var _forecastWeatherDaily = MutableLiveData<Response<ForecastWeather>>()
    val forecastWeatherDaily = _forecastWeatherDaily


//    private fun getCurrentWeather(){
//        viewModelScope.launch (Dispatchers.IO) {
//            //_currentWeather.value = weatherRepository.getCurrentWeather().body()
//            _currentWeather.postValue(weatherRepository.getCurrentWeather())
//        }
//    }

    fun getForecastWeatherHourly(){
        viewModelScope.launch (Dispatchers.IO) {
            _forecastWeatherHourly.postValue(weatherRepository.getWeatherForecastHourly())
        }
    }

    fun getForecastWeatherDaily(){
    viewModelScope.launch (Dispatchers.IO) {
        _forecastWeatherDaily.postValue(weatherRepository.getWeatherForecastDaily())
    }
    }

}