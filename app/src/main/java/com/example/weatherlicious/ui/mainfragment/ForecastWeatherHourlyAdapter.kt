package com.example.weatherlicious.ui.mainfragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherlicious.data.model.forecastweather.ForecastDay
import com.example.weatherlicious.databinding.WeatherHourlyRecyclerviewItemBinding
import com.example.weatherlicious.util.DateFormatter.Companion.dateToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.timeFormatterHourMin

class ForecastWeatherHourlyAdapter(): RecyclerView.Adapter<ForecastWeatherHourlyAdapter.ViewHolder>() {

    private var dataSet = emptyList<ForecastDay>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = WeatherHourlyRecyclerviewItemBinding
            .inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val forecastDay: ForecastDay = dataSet[position]
            holder.bind(forecastDay, position)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun setData(listOfForecastDay: List<ForecastDay>){
        this.dataSet = listOfForecastDay
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val itemBinding: WeatherHourlyRecyclerviewItemBinding
    ): RecyclerView.ViewHolder(itemBinding.root){

        fun bind(forecastDay: ForecastDay, position: Int){
            itemBinding.apply {
                //tvTime.text = forecastWeather.forecast.forecastday[0].hour[0].time
                //tvTime.text = forecastWeatherHourly.time
                tvTime.text = forecastDay.hour[position].time //.dateToMillis().timeFormatterHourMin()

                //Glide.with(itemView).load(forecastWeather.forecast.forecastday[0].hour[0].condition.icon).centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(ivWeatherImage)
                //Glide.with(itemView).load(forecastDay.hour[0].condition.icon).centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(ivWeatherImage)

                //tvTemp.text = forecastWeather.forecast.forecastday[0].hour[0].temp_c.toString()
                //tvTemp.text = "${forecastWeatherHourly.temp_c.toInt()} °"
                tvTemp.text = "${forecastDay.hour[position].temp_c.toInt()}°"

                //tvRainProbability.text = "${forecastWeather.forecast.forecastday[0].hour[0].chance_of_rain} %"
                //tvRainProbability.text = "${forecastWeatherHourly.chance_of_rain} %"
                tvRainProbability.text = "${forecastDay.hour[position].chance_of_rain} %"
            }
        }
    }
}