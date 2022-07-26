package com.example.weatherlicious.di.module

import com.example.weatherlicious.data.source.local.WeatherLocal
import com.example.weatherlicious.data.source.local.WeatherLocalImpl
import com.example.weatherlicious.data.source.remote.WeatherRemote
import com.example.weatherlicious.data.source.remote.WeatherRemoteImpl
import com.example.weatherlicious.data.source.remote.api.WeatherAPI
import com.example.weatherlicious.data.source.repository.WeatherRepository
import com.example.weatherlicious.data.source.repository.WeatherRepositoryImpl
import com.example.weatherlicious.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideRepository(weatherLocal: WeatherLocal, weatherRemote: WeatherRemote): WeatherRepository{
        return WeatherRepositoryImpl(weatherLocal, weatherRemote)
    }

    @Provides
    @Singleton
    fun provideLocalRepository(): WeatherLocal{
        return WeatherLocalImpl()
    }

    @Provides
    @Singleton
    fun provideRemoteRepository(weatherAPI: WeatherAPI): WeatherRemote{
        return WeatherRemoteImpl(weatherAPI)
    }
}