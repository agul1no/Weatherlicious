package com.example.weatherlicious.ui.mainfragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.util.NestedScrollViewListener
import com.example.weatherlicious.R
import com.example.weatherlicious.data.model.forecastweather.ForecastDay
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.databinding.FragmentMainBinding
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateDayMonthYearHourMin
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHour
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter.Companion.categorizeUVValue
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter.Companion.transformWindDirectionResponse
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //private lateinit var adapter: ForecastWeatherHourlyAdapter
    private lateinit var adapterHourly: WeatherForecastAdapterHourly
    private lateinit var adapterDaily: WeatherForecastAdapterDaily

    private val mainFragmentViewModel: MainFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        mainFragmentViewModel.getForecastWeatherHourly()
        mainFragmentViewModel.getForecastWeatherDaily()
        initializeRecyclerViewHourly(mainFragmentViewModel)
        initializeRecyclerViewDaily(mainFragmentViewModel)

        hideWeatherImage()
        observeCurrentWeather()
        observeForecastWeatherHourly()
        observeForecastWeatherDaily()
        //listenNestedScroll(mainFragmentViewModel, context!!)
        setToolbarItemListener()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainFragmentViewModel.getForecastWeatherHourly()
        mainFragmentViewModel.getForecastWeatherDaily()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initializeRecyclerViewHourly(mainFragmentViewModel: MainFragmentViewModel): RecyclerView {
        //adapter = ForecastWeatherHourlyAdapter()
        adapterHourly = WeatherForecastAdapterHourly()
        binding.recyclerViewHourly.adapter = adapterHourly
        return binding.recyclerViewHourly
    }

    private fun initializeRecyclerViewDaily(mainFragmentViewModel: MainFragmentViewModel): RecyclerView {
        adapterDaily = WeatherForecastAdapterDaily()
        binding.recyclerViewDaily.adapter = adapterDaily
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

    private fun observeCurrentWeather() {
        mainFragmentViewModel.forecastWeatherHourly.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful){
                bindDataForForecastWeatherHourly(response.body()!!)
                bindCurrentWeatherExtraData(response.body()!!)
            }else{
                binding.tvCityName.text = response.code().toString()
            }
        }
    }

    private fun bindDataForForecastWeatherHourly(forecastWeather: ForecastWeather){
        binding.apply {
            collapsingToolbar.title = forecastWeather?.location!!.name
            tvCityName.text = forecastWeather.location.name
            tvDate.text = forecastWeather.location.localtime.dateYearMonthDayHourMinToMillis().millisToDateDayMonthYearHourMin()
            tvTemperature.text = "${forecastWeather.current.temp_c.toInt()}°"
            tvFeelsLike.text = "Feelslike:  ${forecastWeather.current.feelslike_c.toInt()}°"
            tvWindKPH.text = "Wind:  ${forecastWeather.current.wind_kph.toInt()} Kph"
            tvMaxUndMinTemp.text = "${forecastWeather.forecast.forecastday[0].day.maxtemp_c.toInt()}° / ${forecastWeather.forecast.forecastday[0].day.mintemp_c.toInt()}°"
            tvConditionText.text = forecastWeather.current.condition.text
            Glide.with(context!!).load("https:${forecastWeather.current.condition.icon}")
                .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                //.placeholder(R.mipmap.weatherlicious_logo)
                .into(ivConditionIcon)
        }
    }

    private fun observeForecastWeatherHourly(){
        mainFragmentViewModel.forecastWeatherHourly.observe(viewLifecycleOwner) { response ->
            val listOfHours = createListOfForecastWeatherHourly(response)
            adapterHourly.submitList(listOfHours)
        }
    }

    private fun createListOfForecastWeatherHourly(response: Response<ForecastWeather>): List<Hour>{
        val listOfHours = mutableListOf<Hour>()
        var timeCounter = getTime().toInt()
        var firstObject = 0
        for (i in 0..23){
            if (timeCounter == 23){
                listOfHours.add(response.body()?.forecast!!.forecastday[firstObject].hour[timeCounter])
                firstObject = 1
                timeCounter = 0
            }
            listOfHours.add(response.body()?.forecast!!.forecastday[firstObject].hour[timeCounter])
            timeCounter++
        }
        return listOfHours
    }

    private fun getTime(): String {
        return Calendar.getInstance().timeInMillis.millisToHour()
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

    private fun observeForecastWeatherDaily(){
        mainFragmentViewModel.forecastWeatherDaily.observe(viewLifecycleOwner) { response ->
            val listOfDays = createListOfForecastWeatherDaily(response)
            adapterDaily.submitList(listOfDays)
        }
    }

    private fun createListOfForecastWeatherDaily(response: Response<ForecastWeather>): List<ForecastDay>{
        val listOfDays = mutableListOf<ForecastDay>()
        for (i in 0..2){
            listOfDays.add(response.body()?.forecast!!.forecastday[i])
        }
        return listOfDays
    }

    private fun bindCurrentWeatherExtraData(forecastWeather: ForecastWeather){
        binding.apply {
            tvUVValue.text = categorizeUVValue(forecastWeather.forecast.forecastday[0].day.uv)
            tvSunriseTime.text = forecastWeather.forecast.forecastday[0].astro.sunrise
            tvSunsetTime.text = forecastWeather.forecast.forecastday[0].astro.sunset
            tvWindValue.text = transformWindDirectionResponse(forecastWeather.current.wind_dir)
            tvHumidityValue.text = "${forecastWeather.current.humidity} %"
        }
    }

    private fun listenNestedScroll(mainFragmentViewModel: MainFragmentViewModel, context: Context){
        val transitionListener = NestedScrollViewListener(mainFragmentViewModel, context)
        binding.nestedScrollView.setOnScrollChangeListener(transitionListener)
    }

    private fun setToolbarItemListener(){
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            navigateToAddFragment(menuItem)
        }
    }

    private fun navigateToAddFragment (menuItem: MenuItem) : Boolean{
        return when (menuItem.itemId) {
            R.id.action_add -> {
                // Navigate to add screen
                findNavController().navigate(R.id.action_mainFragment_to_addFragment)
                true
            }

            else -> false
        }
    }

}