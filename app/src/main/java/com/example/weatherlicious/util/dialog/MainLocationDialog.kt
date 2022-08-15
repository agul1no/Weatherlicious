package com.example.weatherlicious.util.dialog

import android.app.AlertDialog
import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.ui.addfragment.AddFragmentDirections
import com.example.weatherlicious.ui.addfragment.AddFragmentViewModel

class MainLocationDialog(
    private val context: Context,
    private val addFragmentViewModel: AddFragmentViewModel
    ) {

    fun createMainLocationAlterDialog(cityItem: CityItem, navController: NavController){
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Do you want to make ${cityItem.name} your main location?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            // add the city to the DB with main location value 1
            val city = City(
                cityItem.id.toLong(),
                cityItem.name,
                cityItem.region,
                cityItem.country,
                cityItem.lat,
                cityItem.lon,
                cityItem.url,
                1
            )
            addFragmentViewModel.changeMainLocationFromDBToZero()
            addFragmentViewModel.insertCity(city)
            dialog.cancel()
            navController.navigate(AddFragmentDirections.actionAddFragmentToMainFragment())
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // add the city to the DB with main location value 0
            val city = City(
                cityItem.id.toLong(),
                cityItem.name,
                cityItem.region,
                cityItem.country,
                cityItem.lat,
                cityItem.lon,
                cityItem.url,
                0
            )
            addFragmentViewModel.insertCity(city)
            dialog.cancel()
            navController.navigate(AddFragmentDirections.actionAddFragmentToMainFragment())
        }
        val alterDialog = alertDialogBuilder.create()
        alterDialog.show()
    }
}