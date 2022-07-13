package com.example.weatherlicious

import android.os.Bundle
import android.text.Layout
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
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

        createMenuAddIcon()

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
                binding.collapsingToolbar.title = "Expanded"
            }
            if(percent > 0.8f){
                binding.collapsingToolbar.title = "Collapsed"
            }

        })
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        //super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.add_menu_main_fragment, menu)
//    }

}