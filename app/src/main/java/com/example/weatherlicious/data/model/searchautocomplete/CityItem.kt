package com.example.weatherlicious.data.model.searchautocomplete

data class CityItem(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
)
