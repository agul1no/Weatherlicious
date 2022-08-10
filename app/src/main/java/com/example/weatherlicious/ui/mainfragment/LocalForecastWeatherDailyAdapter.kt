package com.example.weatherlicious.ui.mainfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.data.source.local.entities.LocalForecastWeatherDaily
import com.example.weatherlicious.databinding.WeatherDailyRecycleViewItemBinding
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateYearMonthDay
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDayOfTheWeek
import java.util.*

class LocalForecastWeatherDailyAdapter : ListAdapter<LocalForecastWeatherDaily, LocalForecastWeatherDailyAdapter.ViewHolder>(WeatherForecastDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalForecastWeatherDailyAdapter.ViewHolder {
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

        fun bind(localForecastWeatherDaily: LocalForecastWeatherDaily){
            itemBinding.apply {
                if (localForecastWeatherDaily.dayOfTheWeek == getDay()){
                    tvDayOfTheWeek.text = "Today"
                }else{
                    tvDayOfTheWeek.text = localForecastWeatherDaily.dayOfTheWeek
                }
                tvChanceOfRain.text = localForecastWeatherDaily.chanceOfRain
                Glide.with(itemView).load(localForecastWeatherDaily.iconDay)
                    .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivWeatherDay)
                Glide.with(itemView).load(localForecastWeatherDaily.iconNight)
                    .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivWeatherNight)
                tvMaxUndMinTemp.text = localForecastWeatherDaily.maxAndMinTemp
            }
        }

        private fun getDay(): String {
            return Calendar.getInstance().timeInMillis.millisToDateYearMonthDay()
        }
    }

    class WeatherForecastDiffCallBack : DiffUtil.ItemCallback<LocalForecastWeatherDaily>() {
        override fun areItemsTheSame(oldItem: LocalForecastWeatherDaily, newItem: LocalForecastWeatherDaily): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocalForecastWeatherDaily, newItem: LocalForecastWeatherDaily): Boolean {
            return oldItem == newItem
        }
    }
}