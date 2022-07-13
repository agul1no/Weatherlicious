package com.example.weatherlicious.ui.mainfragment

import androidx.lifecycle.ViewModel
import com.example.weatherlicious.data.source.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    weatherRepository: WeatherRepository
): ViewModel() {
    

}