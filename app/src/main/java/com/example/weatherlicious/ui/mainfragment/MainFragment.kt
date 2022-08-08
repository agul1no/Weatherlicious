package com.example.weatherlicious.ui.mainfragment

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.R
import com.example.weatherlicious.data.model.forecastweather.ForecastDay
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.databinding.FragmentMainBinding
import com.example.weatherlicious.util.ConnectionLiveData
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter.Companion.categorizeUVValue
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter.Companion.transformWindDirectionResponse
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateDayMonthYearHourMin
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHour
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //private lateinit var adapter: ForecastWeatherHourlyAdapter
    private lateinit var adapterRemoteForecastWeatherHourly: RemoteWeatherForecastAdapterHourly
    private lateinit var adapterRemoteForecastWeatherDaily: WeatherForecastAdapterDaily
    private lateinit var adapterLocalForecastWeatherHourly: LocalForecastWeatherHourlyAdapter
    //private lateinit var adapterLocalForecastWeatherDaily: WeatherForecastAdapterDaily

    private val mainFragmentViewModel: MainFragmentViewModel by viewModels()
    lateinit var connectionLiveData: ConnectionLiveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        initializeRecyclerViewHourly()
        initializeRecyclerViewDaily()

        hideWeatherImage()
        setToolbarItemListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if(!isNetworkAvailable(context)){
            mainFragmentViewModel.getLocalCurrentWeather()
            mainFragmentViewModel.getLocalForecastWeatherHourly()
            observeLocalCurrentWeather()
            observeLocalForecastWeatherHourly()
            Toast.makeText(context, "Starting... internet is not available", Toast.LENGTH_SHORT).show()
        }
        connectionLiveData = ConnectionLiveData(context!!)
        connectionLiveData.observe(viewLifecycleOwner) { isNetworkAvailable ->
            if (isNetworkAvailable) {
                mainFragmentViewModel.getRemoteForecastWeatherHourly()
                mainFragmentViewModel.getRemoteForecastWeatherDaily()
                mainFragmentViewModel.transformRemoteForecastWeatherIntoLocalCurrentWeather(context!!)
                mainFragmentViewModel.transformRemoteForecastWeatherIntoLocalForecastWeatherHourly(context!!)
                observeRemoteCurrentWeather()
                observeRemoteForecastWeatherHourly()
                observeRemoteForecastWeatherDaily()
                Toast.makeText(context, "Internet is available", Toast.LENGTH_SHORT).show()

            } else {
                mainFragmentViewModel.getLocalCurrentWeather()
                mainFragmentViewModel.getLocalForecastWeatherHourly()
                observeLocalCurrentWeather()
                observeLocalForecastWeatherHourly()
                Toast.makeText(context, "Internet is not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    private fun initializeRecyclerViewHourly(): RecyclerView {
        //adapter = ForecastWeatherHourlyAdapter()
        adapterRemoteForecastWeatherHourly = RemoteWeatherForecastAdapterHourly()
        binding.recyclerViewHourly.adapter = adapterRemoteForecastWeatherHourly
        return binding.recyclerViewHourly
    }

    private fun initializeRecyclerViewDaily(): RecyclerView {
        adapterRemoteForecastWeatherDaily = WeatherForecastAdapterDaily()
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

    private fun observeRemoteCurrentWeather() {
        mainFragmentViewModel.forecastWeatherHourly.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful){
                bindDataRemoteCurrentWeather(response.body()!!)
                bindCurrentWeatherExtraData(response.body()!!)
            }else{
                binding.tvCityName.text = response.code().toString()
            }
        }
    }

    private fun bindDataRemoteCurrentWeather(forecastWeather: ForecastWeather){
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

    private fun observeRemoteForecastWeatherHourly(){
        mainFragmentViewModel.forecastWeatherHourly.observe(viewLifecycleOwner) { response ->
            adapterRemoteForecastWeatherHourly = RemoteWeatherForecastAdapterHourly()
            binding.recyclerViewHourly.adapter = adapterRemoteForecastWeatherHourly
            val listOfHours = createListOfForecastWeatherHourly(response)
            adapterRemoteForecastWeatherHourly.submitList(listOfHours)
        }
    }

    private fun createListOfForecastWeatherHourly(response: Response<ForecastWeather>): List<Hour>{
        val listOfHours = mutableListOf<Hour>()
        var timeCounter = getCurrentTime().toInt()
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

    private fun getCurrentTime(): String {
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

    private fun observeRemoteForecastWeatherDaily(){
        mainFragmentViewModel.forecastWeatherDaily.observe(viewLifecycleOwner) { response ->
            val listOfDays = createListOfForecastWeatherDaily(response)
            adapterRemoteForecastWeatherDaily.submitList(listOfDays)
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

    private fun observeLocalCurrentWeather(){
        mainFragmentViewModel.localCurrentWeather.observe(viewLifecycleOwner) { localCurrentWeather ->
            bindDataLocalCurrentWeather(localCurrentWeather)
        }
    }

    private fun bindDataLocalCurrentWeather(localCurrentWeather: LocalCurrentWeather){
        binding.apply {
            collapsingToolbar.title = localCurrentWeather.cityName
            tvCityName.text = localCurrentWeather.cityName
            tvDate.text = localCurrentWeather.date
            tvTemperature.text = localCurrentWeather.temperature
            tvFeelsLike.text = localCurrentWeather.feelsLike
            tvWindKPH.text = localCurrentWeather.wind
            tvMaxUndMinTemp.text = localCurrentWeather.maxAndMinTemp
            tvConditionText.text = localCurrentWeather.condition
            Glide.with(context!!).load(localCurrentWeather.icon)
                .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                .into(ivConditionIcon)
        }
    }

    private fun observeLocalForecastWeatherHourly(){
        mainFragmentViewModel.localForecastWeatherHourly.observe(viewLifecycleOwner) { listOfLocalForecastWeatherHourly ->
            //TODO create another adapter for a list of LocalForecastWeatherHourly
            adapterLocalForecastWeatherHourly = LocalForecastWeatherHourlyAdapter()
            binding.recyclerViewHourly.adapter = adapterLocalForecastWeatherHourly
            adapterLocalForecastWeatherHourly.submitList(listOfLocalForecastWeatherHourly)
        }
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