package com.example.weatherlicious.util.dialog

import android.app.AlertDialog
import android.content.Context
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import com.example.weatherlicious.data.source.local.entities.City

class ConfirmDialog(private val context: Context) {

    fun createConfirmAlterDialog(cityItem: CityItem){
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage("Do you want add ${cityItem.name} to the List?")
        alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
            dialog.cancel()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }
        val alterDialog = alertDialogBuilder.create()
        alterDialog.show()
    }
}