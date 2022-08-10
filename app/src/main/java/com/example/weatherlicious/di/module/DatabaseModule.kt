package com.example.weatherlicious.di.module

import android.app.Application
import androidx.room.Room
import com.example.weatherlicious.data.source.local.WeatherDatabase
import com.example.weatherlicious.util.BitmapConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): WeatherDatabase =
        Room.databaseBuilder(application, WeatherDatabase::class.java, "weatherlicious_database")
            .addTypeConverter(BitmapConverter())
            .build()
}