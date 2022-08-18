package com.example.weatherlicious.ui.addfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
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
class AddFragmentViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    private var _citySearch = MutableLiveData<Resource<List<CityItem>>>()
    val citySearch = _citySearch

    private fun searchForCity(name: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                _citySearch.postValue(Resource.Loading())
                val response = weatherRepository.searchForCity(name)
                _citySearch.postValue(handleCitySearchResponse(response))
            }catch (socketTimeoutException: SocketTimeoutException){

            }catch (ioException: IOException){

            }
        }
    }

    private fun handleCitySearchResponse(response: Response<List<CityItem>>): Resource<List<CityItem>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

   fun checkQueryIsEmpty(query: String?){
        if (query?.isEmpty() == true){

        }
        if (query?.isNotEmpty() == true){
            searchForCity(query)
        }
    }

    fun insertCity(city:City){
        viewModelScope.launch (Dispatchers.IO) {
            weatherRepository.insertCity(city)
        }
    }

    fun changeMainLocationFromDBToZero(){
        viewModelScope.launch (Dispatchers.IO) {
            weatherRepository.changeMainLocationFromDBToZero()
        }
    }

    fun insertCityResettingMainLocation(city: City){
        viewModelScope.launch (Dispatchers.IO) {
            weatherRepository.changeMainLocationFromDBToZero()
            weatherRepository.insertCity(city)
        }
    }

    fun getLocationList(): LiveData<List<City>>{
        return weatherRepository.getLocationsList()
    }

    fun getLocationListNames(): LiveData<List<String>>{
        return weatherRepository.getLocationsListName()
    }
}