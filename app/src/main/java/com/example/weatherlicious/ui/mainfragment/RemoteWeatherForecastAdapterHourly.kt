package com.example.weatherlicious.ui.mainfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.databinding.WeatherHourlyRecyclerviewItemBinding
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHourMin

class RemoteWeatherForecastAdapterHourly : ListAdapter<Hour, RemoteWeatherForecastAdapterHourly.ViewHolder>(WeatherForecastDiffCallBack()) {

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
                tvTime.text = forecastWeatherHourly.time.dateYearMonthDayHourMinToMillis().millisToHourMin()
                tvTemp.text = "${forecastWeatherHourly.temp_c.toInt()}°"
                tvRainProbability.text = "${forecastWeatherHourly.chance_of_rain} %"
                Glide.with(itemView).load("https:${forecastWeatherHourly.condition.icon}")
                    .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivWeatherImage)
            }
        }
    }

    class WeatherForecastDiffCallBack : DiffUtil.ItemCallback<Hour>() {
        override fun areItemsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem.time_epoch == newItem.time_epoch
        }

        override fun areContentsTheSame(oldItem: Hour, newItem: Hour): Boolean {
            return oldItem == newItem
        }
    }
}