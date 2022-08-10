package com.example.weatherlicious.ui.mainfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherHourly
import com.example.weatherlicious.databinding.WeatherHourlyRecyclerviewItemBinding
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHourMin

class LocalForecastWeatherHourlyAdapter: ListAdapter<LocalForecastWeatherHourly, LocalForecastWeatherHourlyAdapter.ViewHolder>(WeatherForecastDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = WeatherHourlyRecyclerviewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val localForecastWeatherHourly = getItem(position)
        holder.bind(localForecastWeatherHourly)
    }

    inner class ViewHolder(
        private val itemBinding: WeatherHourlyRecyclerviewItemBinding
    ): RecyclerView.ViewHolder(itemBinding.root){

        fun bind(localForecastWeatherHourly: LocalForecastWeatherHourly){
            itemBinding.apply {
                tvTime.text = localForecastWeatherHourly.hour //.dateYearMonthDayHourMinToMillis().millisToHourMin()
                tvTemp.text = localForecastWeatherHourly.temperature //"${forecastWeatherHourly.temp_c.toInt()}°"
                tvRainProbability.text = localForecastWeatherHourly.chanceOfRain //"${forecastWeatherHourly.chance_of_rain} %"
                Glide.with(itemView).load(localForecastWeatherHourly.icon)
                    .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivWeatherImage)
            }
        }
    }

    class WeatherForecastDiffCallBack : DiffUtil.ItemCallback<LocalForecastWeatherHourly>() {
        override fun areItemsTheSame(oldItem: LocalForecastWeatherHourly, newItem: LocalForecastWeatherHourly): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocalForecastWeatherHourly, newItem: LocalForecastWeatherHourly): Boolean {
            return oldItem == newItem
        }
    }
}