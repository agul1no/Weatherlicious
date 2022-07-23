package com.example.weatherlicious.util

import android.content.Context
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.widget.NestedScrollView
import com.example.weatherlicious.ui.mainfragment.MainFragmentViewModel

class NestedScrollViewListener(
    private val mainFragmentViewModel: MainFragmentViewModel,
    private val context: Context
    ): NestedScrollView.OnScrollChangeListener {

    override fun onScrollChange(
        v: NestedScrollView,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        //Toast.makeText(context, "${v.getChildAt(0).measuredHeight}", Toast.LENGTH_SHORT).show()
        //Toast.makeText(context, "${oldScrollY}", Toast.LENGTH_SHORT).show()
        if (oldScrollY == 0){
            mainFragmentViewModel.getForecastWeatherHourly()
            Toast.makeText(context, "Weather Request send", Toast.LENGTH_SHORT).show()
        }

    }

}