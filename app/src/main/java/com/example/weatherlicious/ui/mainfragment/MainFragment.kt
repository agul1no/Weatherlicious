package com.example.weatherlicious.ui.mainfragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.R
import com.example.weatherlicious.data.model.forecastweather.ForecastWeather
import com.example.weatherlicious.data.model.forecastweather.Hour
import com.example.weatherlicious.databinding.FragmentMainBinding
import com.example.weatherlicious.util.DateFormatter.Companion.timeFormatterHour
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import java.util.*

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    //private lateinit var adapter: ForecastWeatherHourlyAdapter
    private lateinit var adapter: WeatherForecastAdapter

    private val mainFragmentViewModel: MainFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        mainFragmentViewModel.getForecastWeatherHourly()
        initializeRecyclerView(mainFragmentViewModel)

        hideWeatherImage()
        createMenuAddIcon()
        observeCurrentWeather()
        observeForecastWeatherHourly()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initializeRecyclerView(mainFragmentViewModel: MainFragmentViewModel): RecyclerView {
        //adapter = ForecastWeatherHourlyAdapter()
        adapter = WeatherForecastAdapter()
        binding.recyclerView.adapter = adapter
        return binding.recyclerView
    }

    private fun hideWeatherImage(){
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            // Un-slide the image or container from views
            binding.ivCurrentWeather.translationY = -verticalOffset.toFloat()

            val percent =
                (Math.abs(verticalOffset)).toFloat() / appBarLayout?.totalScrollRange!! // 0F to 1F
            // Control container opacity according to offset
            binding.ivCurrentWeather.alpha = 0.8F - percent
            editDependOnLightOrDarkTheme(percent)
        })
    }

    private fun createMenuAddIcon(){
        activity?.addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_menu_main_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                findNavController().navigate(R.id.action_mainFragment_to_addFragment)
                return true
            }

        },) //viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeCurrentWeather() {
        mainFragmentViewModel.forecastWeatherHourly.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful){
                bindDataForForecastWeatherHourly(response.body()!!)
            }else{
                binding.tvCityName.text = response.code().toString()
            }
        }
    }

    private fun bindDataForForecastWeatherHourly(forecastWeather: ForecastWeather){
        binding.apply {
            collapsingToolbar.title = forecastWeather?.location!!.name
            tvCityName.text = forecastWeather.location.name
            tvDate.text = forecastWeather.location.localtime
            tvTemperature.text = "${forecastWeather.current.temp_c.toInt()}°"
            tvFeelsLike.text = "Feelslike:  ${forecastWeather.current.feelslike_c.toInt()}°"
            tvWindKPH.text = "Wind:  ${forecastWeather.current.wind_kph.toInt()} Kph"
            tvMaxUndMinTemp.text = "${forecastWeather.forecast.forecastday[0].day.maxtemp_c.toInt()}° / ${forecastWeather.forecast.forecastday[0].day.mintemp_c.toInt()}°"
            tvConditionText.text = forecastWeather.current.condition.text
            Glide.with(context!!).load(forecastWeather.current.condition.icon)
                .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.mipmap.weatherlicious_logo)
                .into(ivConditionIcon)
        }
    }

    private fun observeForecastWeatherHourly(){
        mainFragmentViewModel.forecastWeatherHourly.observe(viewLifecycleOwner) { response ->
            val listOfHours = createListOfForecastWeatherHourly(response)
            adapter.submitList(listOfHours)
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
        return Calendar.getInstance().timeInMillis.timeFormatterHour()
    }

    private fun editDependOnLightOrDarkTheme(percent: Float){
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

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        //super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.add_menu_main_fragment, menu)
//    }

}