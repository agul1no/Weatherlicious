package com.example.weatherlicious

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.data.source.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private var _mainLocation = MutableLiveData<String?>()
    val mainLocation = _mainLocation

    private var _listOtherLocations = MutableLiveData<List<City>?>()
    val listOtherLocations = _listOtherLocations


    fun getMainLocationLive() : LiveData<City> {
        return weatherRepository.getMainLocationLive()
    }

    fun getOtherLocations(): LiveData<List<City>>{
        return weatherRepository.getLocationsList()
    }
}