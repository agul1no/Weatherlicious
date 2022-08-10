package com.example.weatherlicious.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherlicious.data.source.local.dao.WeatherDao
import com.example.weatherlicious.data.source.local.entities.*
import com.example.weatherlicious.util.BitmapConverter

@Database(
    entities = [
        City::class,
        LocalCurrentWeather::class,
        LocalForecastWeatherHourly::class,
        LocalForecastWeatherDaily::class,
        LocalCurrentWeatherExtraData::class
               ],
    version = 1,
    exportSchema = false
)
@TypeConverters(BitmapConverter::class)
abstract class WeatherDatabase: RoomDatabase() {

    abstract val weatherDao: WeatherDao
}