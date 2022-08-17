package com.example.weatherlicious.ui.mainfragment

import android.content.Context
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.data.model.forecastweather.ForecastDay
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeather
import com.example.weatherlicious.data.source.local.entities.LocalCurrentWeatherExtraData
import com.example.weatherlicious.databinding.FragmentMainBinding
import com.example.weatherlicious.util.ConnectionLiveData
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter.Companion.categorizeUVValue
import com.example.weatherlicious.util.CurrentWeatherExtraDataFormatter.Companion.transformWindDirectionResponse
import com.example.weatherlicious.util.DateFormatter.Companion.dateYearMonthDayHourMinToMillis
import com.example.weatherlicious.util.DateFormatter.Companion.millisToDateDayMonthYearHourMin
import com.example.weatherlicious.util.DateFormatter.Companion.millisToHour
import com.example.weatherlicious.util.Resource
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //private lateinit var adapter: ForecastWeatherHourlyAdapter
    private lateinit var adapterRemoteForecastWeatherHourly: RemoteWeatherForecastAdapterHourly
    private lateinit var adapterRemoteForecastWeatherDaily: RemoteWeatherForecastAdapterDaily
    private lateinit var adapterLocalForecastWeatherHourly: LocalForecastWeatherHourlyAdapter
    private lateinit var adapterLocalForecastWeatherDaily: LocalForecastWeatherDailyAdapter

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

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if(!isNetworkAvailable(context)){
            mainFragmentViewModel.getLocalCurrentWeather()
            mainFragmentViewModel.getLocalForecastWeatherHourly()
            mainFragmentViewModel.getLocalForecastWeatherDaily()
            mainFragmentViewModel.getLocalCurrentWeatherExtraData()
            observeLocalCurrentWeather()
            observeLocalForecastWeatherHourly()
            observeLocalForecastWeatherDaily()
            observeLocalCurrentWeatherExtraData()
            //Toast.makeText(context, "Starting... internet is not available", Toast.LENGTH_SHORT).show()
        }
        connectionLiveData = ConnectionLiveData(context!!)
        connectionLiveData.observe(viewLifecycleOwner) { isNetworkAvailable ->
            if (isNetworkAvailable) {
                //mainFragmentViewModel.getRemoteForecastWeatherHourly()
                //mainFragmentViewModel.getRemoteForecastWeatherDaily()
                mainFragmentViewModel.getRemoteForecastWeatherByCity()
                mainFragmentViewModel.transformRemoteForecastWeatherIntoLocalCurrentWeather(context!!)
                mainFragmentViewModel.transformRemoteForecastWeatherIntoLocalForecastWeatherHourly(context!!)
                mainFragmentViewModel.transformRemoteForecastWeatherDailyIntoLocalForecastWeatherDaily(context!!)
                mainFragmentViewModel.transformRemoteCurrentWeatherExtraDataIntoLocalCurrentWeatherExtraData()
                observeRemoteCurrentWeather()
                //observeRemoteForecastWeatherHourly()
                observeRemoteForecastWeatherDaily()
                observeRemoteForecastWeatherHourlyByCity()
                //Toast.makeText(context, "Internet is available", Toast.LENGTH_SHORT).show()
            } else {
                mainFragmentViewModel.getLocalCurrentWeather()
                mainFragmentViewModel.getLocalForecastWeatherHourly()
                mainFragmentViewModel.getLocalForecastWeatherDaily()
                mainFragmentViewModel.getLocalCurrentWeatherExtraData()
                observeLocalCurrentWeather()
                observeLocalForecastWeatherHourly()
                observeLocalForecastWeatherDaily()
                observeLocalCurrentWeatherExtraData()
                //Toast.makeText(context, "Internet is not available", Toast.LENGTH_SHORT).show()
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
        mainFragmentViewModel.remoteForecastWeatherByCity.observe(viewLifecycleOwner) { response ->
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

//    private fun observeRemoteForecastWeatherHourly(){
//        mainFragmentViewModel.remoteForecastWeatherHourly.observe(viewLifecycleOwner) { response ->
//            adapterRemoteForecastWeatherHourly = RemoteWeatherForecastAdapterHourly()
//            binding.recyclerViewHourly.adapter = adapterRemoteForecastWeatherHourly
//            when(response){
//                is Resource.Success -> {
//                    hideProgressbar()
//                    response.data?.let {
//                        val listOfHours = createListOfForecastWeatherHourly(response)
//                        adapterRemoteForecastWeatherHourly.submitList(listOfHours)
//                    }
//                }
//                is Resource.Error -> {
//                    hideProgressbar()
//                    response.message?.let { message ->
//                        Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                is Resource.Loading -> {
//                    showProgressbar()
//                }
//            }
//        }
//    }

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

    private fun observeRemoteForecastWeatherDaily(){
        mainFragmentViewModel.remoteForecastWeatherByCity.observe(viewLifecycleOwner) { response ->
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

    private fun bindRemoteCurrentWeatherExtraData(remoteForecastWeather: RemoteForecastWeather){
        binding.apply {
            tvUVValue.text = categorizeUVValue(remoteForecastWeather.forecast.forecastday[0].day.uv)
            tvSunriseTime.text = remoteForecastWeather.forecast.forecastday[0].astro.sunrise
            tvSunsetTime.text = remoteForecastWeather.forecast.forecastday[0].astro.sunset
            tvWindValue.text = transformWindDirectionResponse(remoteForecastWeather.current.wind_dir)
            tvHumidityValue.text = "${remoteForecastWeather.current.humidity} %"
        }
    }

    private fun observeRemoteForecastWeatherHourlyByCity(){
        mainFragmentViewModel.remoteForecastWeatherByCity.observe(viewLifecycleOwner){ response ->
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

    /** local current weather **/

    private fun observeLocalCurrentWeather(){
        mainFragmentViewModel.localCurrentWeather.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let { localCurrentWeather ->
                        bindDataLocalCurrentWeather(localCurrentWeather)
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

    private fun bindDataLocalCurrentWeather(localCurrentWeather: LocalCurrentWeather){
        binding.apply {
                collapsingToolbar.title = localCurrentWeather.cityName
                tvCityName.text = localCurrentWeather.cityName
                tvDate.text = "Last update: ${localCurrentWeather.date}"
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
            adapterLocalForecastWeatherHourly = LocalForecastWeatherHourlyAdapter()
            binding.recyclerViewHourly.adapter = adapterLocalForecastWeatherHourly
            adapterLocalForecastWeatherHourly.submitList(listOfLocalForecastWeatherHourly)
        }
    }

    private fun observeLocalForecastWeatherDaily(){
        mainFragmentViewModel.localForecastWeatherDaily.observe(viewLifecycleOwner) { listOfLocalForecastWeatherDaily ->
            adapterLocalForecastWeatherDaily = LocalForecastWeatherDailyAdapter()
            binding.recyclerViewDaily.adapter = adapterLocalForecastWeatherDaily
            adapterLocalForecastWeatherDaily.submitList(listOfLocalForecastWeatherDaily)
        }
    }

    private fun observeLocalCurrentWeatherExtraData(){
        mainFragmentViewModel.localCurrentWeatherExtraData.observe(viewLifecycleOwner) { localCurrentWeatherExtraData ->
            if (localCurrentWeatherExtraData != null){
                bindLocalCurrentWeatherExtraData(localCurrentWeatherExtraData)
            }
        }
    }

    private fun bindLocalCurrentWeatherExtraData(localCurrentWeatherExtraData: LocalCurrentWeatherExtraData){
        binding.apply {
            tvUVValue.text = localCurrentWeatherExtraData.uvIndex
            tvSunriseTime.text = localCurrentWeatherExtraData.sunrise
            tvSunsetTime.text = localCurrentWeatherExtraData.sunset
            tvWindValue.text = localCurrentWeatherExtraData.windDirection
            tvHumidityValue.text = localCurrentWeatherExtraData.humidity
        }
    }

    private fun hideProgressbar(){
        binding.progressBar.isVisible = false
    }

    private fun showProgressbar(){
        binding.progressBar.isVisible = true
    }
}