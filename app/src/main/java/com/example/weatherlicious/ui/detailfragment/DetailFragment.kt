package com.example.weatherlicious.ui.detailfragment

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.data.model.forecastweather.ForecastDay
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.databinding.FragmentDetailBinding
import com.example.weatherlicious.databinding.FragmentMainBinding
import com.example.weatherlicious.ui.mainfragment.LocalForecastWeatherDailyAdapter
import com.example.weatherlicious.ui.mainfragment.LocalForecastWeatherHourlyAdapter
import com.example.weatherlicious.ui.mainfragment.RemoteWeatherForecastAdapterDaily
import com.example.weatherlicious.ui.mainfragment.RemoteWeatherForecastAdapterHourly
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateDayMonthYearHourMin
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHour
import com.example.weatherlicious.util.Resource
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterRemoteForecastWeatherHourly: RemoteWeatherForecastAdapterHourly
    private lateinit var adapterRemoteForecastWeatherDaily: RemoteWeatherForecastAdapterDaily
    private lateinit var adapterLocalForecastWeatherHourly: LocalForecastWeatherHourlyAdapter
    private lateinit var adapterLocalForecastWeatherDaily: LocalForecastWeatherDailyAdapter

    private val args: DetailFragmentArgs by navArgs()

    private val detailFragmentViewModel: DetailFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val cityName = args.cityName

        initializeRecyclerViewHourly()
        initializeRecyclerViewDaily()

        hideWeatherImage()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val cityName = args.cityName
        detailFragmentViewModel.getRemoteForecastWeatherByCity(cityName = cityName)
        observeRemoteCurrentWeather()
        observeRemoteForecastWeatherDaily()
        observeRemoteForecastWeatherHourlyByCity()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initializeRecyclerViewHourly(): RecyclerView {
        //adapter = ForecastWeatherHourlyAdapter()
        adapterRemoteForecastWeatherHourly = RemoteWeatherForecastAdapterHourly()
        binding.recyclerViewHourly.adapter = adapterRemoteForecastWeatherHourly
        return binding.recyclerViewHourly
    }

    private fun initializeRecyclerViewDaily(): RecyclerView {
        adapterRemoteForecastWeatherDaily = RemoteWeatherForecastAdapterDaily()
        binding.recyclerViewDaily.adapter = adapterRemoteForecastWeatherDaily
        return binding.recyclerViewDaily
    }

    private fun hideWeatherImage(){
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // Un-slide the image or container from views
            binding.ivCurrentWeather.translationY = -verticalOffset.toFloat()

            val percent =
                (Math.abs(verticalOffset)).toFloat() / appBarLayout?.totalScrollRange!! // 0F to 1F
            // Control container opacity according to offset
            binding.ivCurrentWeather.alpha = 0.8F - percent
            editAppbarImageOnLightOrDarkTheme(percent)
        })
    }

    private fun editAppbarImageOnLightOrDarkTheme(percent: Float){
        when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                binding.gradientBottom.visibility = View.VISIBLE
                if(percent < 0.8f){
                    binding.gradientBottom.isVisible = true
                }
                if(percent > 0.8f){
                    binding.gradientBottom.isVisible = false
                }
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.gradientBottom.visibility = View.GONE
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                binding.gradientBottom.visibility= View.GONE
            }
        }
    }

    private fun observeRemoteCurrentWeather() {
        detailFragmentViewModel.remoteForecastWeatherByCity.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let { remoteForecastWeather ->
                        bindDataRemoteCurrentWeather(remoteForecastWeather)
                        bindRemoteCurrentWeatherExtraData(remoteForecastWeather)
                    }
                }
                is Resource.Error -> {
                    hideProgressbar()
                    response.message?.let { message ->
                        Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressbar()
                }
            }
        }
    }

    private fun bindDataRemoteCurrentWeather(remoteForecastWeather: RemoteForecastWeather){
        binding.apply {
            collapsingToolbar.title = remoteForecastWeather?.location!!.name
            tvCityName.text = remoteForecastWeather.location.name
            tvDate.text = remoteForecastWeather.location.localtime.dateYearMonthDayHourMinToMillis().millisToDateDayMonthYearHourMin()
            tvTemperature.text = "${remoteForecastWeather.current.temp_c.toInt()}°"
            tvFeelsLike.text = "Feelslike:  ${remoteForecastWeather.current.feelslike_c.toInt()}°"
            tvWindKPH.text = "Wind:  ${remoteForecastWeather.current.wind_kph.toInt()} Kph"
            tvMaxUndMinTemp.text = "${remoteForecastWeather.forecast.forecastday[0].day.maxtemp_c.toInt()}° / ${remoteForecastWeather.forecast.forecastday[0].day.mintemp_c.toInt()}°"
            tvConditionText.text = remoteForecastWeather.current.condition.text
            Glide.with(context!!).load("https:${remoteForecastWeather.current.condition.icon}")
                .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                .into(ivConditionIcon)
        }
    }

    private fun bindRemoteCurrentWeatherExtraData(remoteForecastWeather: RemoteForecastWeather){
        binding.apply {
            tvUVValue.text =
                CurrentWeatherExtraDataFormatter.categorizeUVValue(remoteForecastWeather.forecast.forecastday[0].day.uv)
            tvSunriseTime.text = remoteForecastWeather.forecast.forecastday[0].astro.sunrise
            tvSunsetTime.text = remoteForecastWeather.forecast.forecastday[0].astro.sunset
            tvWindValue.text = CurrentWeatherExtraDataFormatter.transformWindDirectionResponse(
                remoteForecastWeather.current.wind_dir
            )
            tvHumidityValue.text = "${remoteForecastWeather.current.humidity} %"
        }
    }

    private fun observeRemoteForecastWeatherDaily(){
        detailFragmentViewModel.remoteForecastWeatherByCity.observe(viewLifecycleOwner) { response ->
            adapterRemoteForecastWeatherDaily = RemoteWeatherForecastAdapterDaily()
            binding.recyclerViewDaily.adapter = adapterRemoteForecastWeatherDaily
            when(response){
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let {
                        val listOfDays = createListOfForecastWeatherDaily(response)
                        adapterRemoteForecastWeatherDaily.submitList(listOfDays)
                    }
                }
                is Resource.Error -> {
                    hideProgressbar()
                    response.message?.let { message ->
                        Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressbar()
                }
            }
        }
    }

    private fun createListOfForecastWeatherDaily(response: Resource<RemoteForecastWeather>): List<ForecastDay>{
        val sizeListOfDays = response.data?.forecast?.forecastday?.size!!
        val listOfDays = mutableListOf<ForecastDay>()
        for (i in 0..sizeListOfDays.minus(1)){
            listOfDays.add(response.data?.forecast!!.forecastday[i])
        }
        return listOfDays
    }

    private fun observeRemoteForecastWeatherHourlyByCity(){
        detailFragmentViewModel.remoteForecastWeatherByCity.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let {
                        val listOfHours = createListOfForecastWeatherHourly(response)
                        adapterRemoteForecastWeatherHourly.submitList(listOfHours)
                    }
                }
                is Resource.Error -> {
                    hideProgressbar()
                    response.message?.let { message ->
                        Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressbar()
                }
            }
        }
    }

    private fun createListOfForecastWeatherHourly(response: Resource<RemoteForecastWeather>): List<Hour>{
        val listOfHours = mutableListOf<Hour>()
        var timeCounter = getCurrentTime().toInt()
        var firstObject = 0
        for (i in 0..23){
            if (timeCounter == 23){
                listOfHours.add(response.data?.forecast!!.forecastday[firstObject].hour[timeCounter])
                firstObject = 1
                timeCounter = 0
            }
            listOfHours.add(response.data?.forecast!!.forecastday[firstObject].hour[timeCounter])
            timeCounter++
        }
        return listOfHours
    }

    private fun getCurrentTime(): String {
        return Calendar.getInstance().timeInMillis.millisToHour()
    }

    private fun hideProgressbar(){
        binding.progressBar.isVisible = false
    }

    private fun showProgressbar(){
        binding.progressBar.isVisible = true
    }

}