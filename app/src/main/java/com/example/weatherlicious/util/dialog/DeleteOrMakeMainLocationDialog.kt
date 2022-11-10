package com.example.weatherlicious.util.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.core.view.get
import androidx.navigation.NavController
import com.example.weatherlicious.MainActivity
import com.example.weatherlicious.MainActivityViewModel
import com.example.weatherlicious.R
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.ui.mainfragment.MainFragmentDirections
import com.example.weatherlicious.util.Constants
import com.google.android.material.navigation.NavigationView

class DeleteOrMakeMainLocationDialog(
    private val context: Context,
    private val mainActivityViewModel: MainActivityViewModel
) {

    fun createDeleteOrMakeMainLocationDialog(navController: NavController, city: City, mainActivity: MainActivity, intent: Intent) {
        val deleteOrMakeMainLocationDialog = Dialog(context, R.style.Theme_Dialog)
        deleteOrMakeMainLocationDialog.setCancelable(false)
        deleteOrMakeMainLocationDialog.setTitle("Select an option")
        try {
            deleteOrMakeMainLocationDialog.setContentView(R.layout.delete_dialog)
        }catch (e: Exception){
            Log.d("DeleteOrMakeMainLocationDialog", e.toString())
        }

        val makeMainLocationButton = deleteOrMakeMainLocationDialog.findViewById<Button>(R.id.buttonMakeMainLocation)
        makeMainLocationButton.text = "Set ${city.name} as main location"
        val deleteButton = deleteOrMakeMainLocationDialog.findViewById<Button>(R.id.buttonDelete)
        deleteButton.text = "Delete ${city.name}"
        val detailButton = deleteOrMakeMainLocationDialog.findViewById<Button>(R.id.buttonDetails)
        detailButton.text = "Show ${city.name}'s weather"
        val cancelButton = deleteOrMakeMainLocationDialog.findViewById<Button>(R.id.buttonCancel)
        deleteOrMakeMainLocationDialog.show()

        makeMainLocationButton.setOnClickListener {
            deleteOrMakeMainLocationDialog.dismiss()
            val cityToInsert = City(
                city.id,
                city.name,
                city.region,
                city.country,
                city.lat,
                city.lon,
                city.url,
                1
            )
            mainActivityViewModel.insertCityResettingMainLocation(cityToInsert)
            mainActivity.finish()
            mainActivity.startActivity(intent)
        }

        deleteButton.setOnClickListener {
            deleteOrMakeMainLocationDialog.dismiss()
            mainActivityViewModel.deleteCity(city)
            mainActivity.finish()
            mainActivity.startActivity(intent)
        }

        detailButton.setOnClickListener {
            deleteOrMakeMainLocationDialog.dismiss()
            navController.navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(city.name))
        }

        cancelButton.setOnClickListener {
            deleteOrMakeMainLocationDialog.dismiss()
        }
    }
}