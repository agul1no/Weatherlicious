package com.example.weatherlicious

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherlicious.databinding.FragmentMainBinding
import com.google.android.material.appbar.AppBarLayout

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        hideWeatherImage()

        return binding.root
    }

    private fun hideWeatherImage(){
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->

            binding.ivCurrentWeather.translationY =
                -verticalOffset.toFloat() // Un-slide the image or container from views

            val percent =
                (Math.abs(verticalOffset)).toFloat() / appBarLayout?.totalScrollRange!! // 0F to 1F
            // Control container opacity according to offset
            binding.ivCurrentWeather.alpha = 1F - percent
            //binding.ivCurrentWeather.scaleY = (1F - percent) + percent / 1.199F
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}