package com.example.weatherlicious.data.model.currentweather

data class CurrentWeather(
    val location: Location,
    val current: Current
)

//data class CurrentWeather(
//    @SerializedName("name")
//    val cityName: String,
//    val region: String,
//    val country: String,
//    @SerializedName("lat")
//    val latitude: Double,
//    @SerializedName("lon")
//    val longitude: Double,
//    @SerializedName("tz_id")
//    val continent: String,
//    @SerializedName("localtime_epoch")
//    val localTimeEpoch: Long,
//    @SerializedName("localtime")
//    val localTimeString: String,
//    @SerializedName("last_updated_epoch")
//    val lastUpdatedEpoch: Long,
//    @SerializedName("last_updated")
//    val lastUpdatedString: String,
//    @SerializedName("temp_c")
//    val tempC: Double,
//    @SerializedName("temp_f")
//    val tempF: Double,
//    @SerializedName("is_day")
//    val isDay: Int,
//    @SerializedName("wind_mph")
//    val windMPH: Double,
//    @SerializedName("wind_kph")
//    val windKPH: Double,
//)