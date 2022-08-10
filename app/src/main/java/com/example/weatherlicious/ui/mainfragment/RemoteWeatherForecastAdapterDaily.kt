package com.example.weatherlicious.ui.mainfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.data.model.forecastweather.ForecastDay
import com.example.weatherlicious.databinding.WeatherDailyRecycleViewItemBinding
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateYearMonthDay
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDayOfTheWeek
import java.util.*

class RemoteWeatherForecastAdapterDaily : ListAdapter<ForecastDay, RemoteWeatherForecastAdapterDaily.ViewHolder>(WeatherForecastDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RemoteWeatherForecastAdapterDaily.ViewHolder {
        val view = WeatherDailyRecycleViewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val forecastDay = getItem(position)
        holder.bind(forecastDay)
    }

    inner class ViewHolder(
        private val itemBinding: WeatherDailyRecycleViewItemBinding
    ): RecyclerView.ViewHolder(itemBinding.root){

        fun bind(forecastWeatherDaily: ForecastDay){
            itemBinding.apply {
                if (forecastWeatherDaily.date == getDay()){
                    tvDayOfTheWeek.text = "Today"
                }else{
                    tvDayOfTheWeek.text = forecastWeatherDaily.date.dateYearMonthDayToMillis().millisToDayOfTheWeek()
                }
                tvChanceOfRain.text = "${forecastWeatherDaily.day.daily_chance_of_rain} %"
                Glide.with(itemView).load("https:${forecastWeatherDaily.day.condition.icon}")
                    .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivWeatherDay)
                Glide.with(itemView).load("https:${forecastWeatherDaily.hour[22].condition.icon}")
                    .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivWeatherNight)
                tvMaxUndMinTemp.text = "${forecastWeatherDaily.day.maxtemp_c.toInt()}° / ${forecastWeatherDaily.day.mintemp_c.toInt()}°"
            }
        }

        private fun getDay(): String {
            return Calendar.getInstance().timeInMillis.millisToDateYearMonthDay()
        }
    }

    class WeatherForecastDiffCallBack : DiffUtil.ItemCallback<ForecastDay>() {
        override fun areItemsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: ForecastDay, newItem: ForecastDay): Boolean {
            return oldItem == newItem
        }
    }
}