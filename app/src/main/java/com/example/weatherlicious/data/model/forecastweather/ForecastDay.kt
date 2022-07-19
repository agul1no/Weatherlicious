package com.example.weatherlicious.data.model.forecastweather

data class ForecastDay(
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)
