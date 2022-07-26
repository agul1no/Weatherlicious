package com.example.weatherlicious.data.model.forecastweather

import com.example.weatherlicious.data.model.currentweather.Condition
import com.example.weatherlicious.data.model.currentweather.Current
import com.example.weatherlicious.data.model.currentweather.Location

data class Forecast(
    val forecastday: List<ForecastDay>
)



