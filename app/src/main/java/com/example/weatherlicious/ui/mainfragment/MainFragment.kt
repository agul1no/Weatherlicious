package com.example.weatherlicious.ui.mainfragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.weatherlicious.R
import com.example.weatherlicious.data.model.currentweather.CurrentWeather
import com.example.weatherlicious.databinding.FragmentMainBinding
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainFragmentViewModel: MainFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        hideWeatherImage()

        createMenuAddIcon()

        mainFragmentViewModel.currentWeather.observe(viewLifecycleOwner) { currentWeather ->
            bindDataFromViewModelToViews(currentWeather)
            Log.d("Debug", currentWeather.toString())
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun hideWeatherImage(){
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            binding.ivCurrentWeather.translationY = -verticalOffset.toFloat() // Un-slide the image or container from views

            val percent =
                (Math.abs(verticalOffset)).toFloat() / appBarLayout?.totalScrollRange!! // 0F to 1F
            // Control container opacity according to offset
            binding.ivCurrentWeather.alpha = 0.8F - percent
            //binding.ivCurrentWeather.scaleY = (1F - percent) + percent / 1.199F
            if(percent < 0.8f){ //expanded
                //binding.collapsingToolbar.title = "Expanded"
                binding.gradientBottom.isVisible = true
            }
            if(percent > 0.8f){
                //binding.collapsingToolbar.title = "Collapsed"
                binding.gradientBottom.isVisible = false
            }

        })
    }

    private fun bindDataFromViewModelToViews(currentWeather: CurrentWeather){
        binding.apply {
            collapsingToolbar.title = currentWeather.location.name
            tvCityName.text = currentWeather.location.name
            tvDate.text = currentWeather.location.localtime
            tvTemperature.text = "${currentWeather.current.temp_c.toInt()}°"
            tvFeelsLike.text = "Feelslike:  ${currentWeather.current.feelslike_c}°"
            tvWindKPH.text = "Wind:  ${currentWeather.current.wind_kph.toInt()} Kph"
            tvConditionText.text = currentWeather.current.condition.text
        }

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        //super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.add_menu_main_fragment, menu)
//    }

}