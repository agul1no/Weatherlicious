package com.example.weatherlicious.util

class CurrentWeatherExtraDataFormatter {

    companion object{

        fun categorizeUVValue(uv: Double): String{
            return when(uv){
                in 0.0..2.99 -> "Low"
                in 3.0..5.99 -> "Medium"
                in 6.0..7.99 -> "High"
                in 8.0..10.99 -> "Very High"
                in 11.0..100.0 -> "Extremely High"
                else -> "Data couldn't be called"
            }
        }

        fun transformWindDirectionResponse(windDirection: String): String{
            return when(windDirection){
                "N" -> "North"
                "W" -> "West"
                "E" -> "East"
                "S" -> "South"
                "SSW" -> "South West"
                "SSE" -> "South East"
                "NNW" -> "North West"
                "NNE" -> "North East"
                else -> "South East"
            }
        }
    }
}