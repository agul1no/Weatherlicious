package com.example.weatherlicious.ui.mainfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherlicious.data.model.forecastweather.ForecastDay
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.databinding.WeatherHourlyRecyclerviewItemBinding
import com.example.weatherlicious.util.DateFormatter.Companion.dateToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.dateToMillisHourMin
import com.example.weatherlicious.util.DateFormatter.Companion.timeFormatterHourMin

class WeatherForecastAdapter : ListAdapter<Hour, WeatherForecastAdapter.ViewHolder>(WeatherForecastDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = WeatherHourlyRecyclerviewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecastDay = getItem(position)
        holder.bind(forecastDay)
    }

    inner class ViewHolder(
        private val itemBinding: WeatherHourlyRecyclerviewItemBinding
    ): RecyclerView.ViewHolder(itemBinding.root){

        fun bind(forecastWeatherHourly: Hour){
            itemBinding.apply {
                //tvTime.text = forecastWeather.forecast.forecastday[0].hour[0].time
                tvTime.text = forecastWeatherHourly.time//.dateToMillisHourMin().timeFormatterHourMin()
                //tvTime.text = forecastDay.hour[positionHour].time //.dateToMillis().timeFormatterHourMin()

                //Glide.with(itemView).load(forecastWeather.forecast.forecastday[0].hour[0].condition.icon).centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(ivWeatherImage)
                //Glide.with(itemView).load(forecastDay.hour[0].condition.icon).centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(ivWeatherImage)

                //tvTemp.text = forecastWeather.forecast.forecastday[0].hour[0].temp_c.toString()
                tvTemp.text = "${forecastWeatherHourly.temp_c.toInt()} °"
                //tvTemp.text = "${forecastDay.hour[positionHour].temp_c.toInt()}°"

                //tvRainProbability.text = "${forecastWeather.forecast.forecastday[0].hour[0].chance_of_rain} %"
                tvRainProbability.text = "${forecastWeatherHourly.chance_of_rain} %"
                //tvRainProbability.text = "${forecastDay.hour[positionHour].chance_of_rain} %"
            }
        }
    }

    class WeatherForecastDiffCallBack : DiffUtil.ItemCallback<Hour>() {
        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem.time_epoch == newItem.time_epoch
        }

        override fun areContentsTheSame(
            oldItem: Hour,
            newItem: Hour
        ): Boolean {
            return oldItem == newItem
        }
    }


}