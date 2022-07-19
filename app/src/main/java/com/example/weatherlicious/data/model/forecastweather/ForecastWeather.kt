package com.example.weatherlicious.data.model.forecastweather

import com.example.weatherlicious.data.model.currentweather.Current
import com.example.weatherlicious.data.model.currentweather.Location

data class ForecastWeather(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)
