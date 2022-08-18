package com.example.weatherlicious.util.dialog

import android.app.AlertDialog
import android.content.Context
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import com.example.weatherlicious.data.source.local.entities.City

class CustomAlertDialog(private val context: Context) {

    fun createCustomAlertDialog(message: String, positiveButtonText: String, negativeButtonText: String){
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(positiveButtonText) { dialog, _ ->
            dialog.cancel()
        }
        alertDialogBuilder.setNegativeButton(negativeButtonText) { dialog, _ ->
            dialog.cancel()
        }
        val alterDialog = alertDialogBuilder.create()
        alterDialog.show()
    }
}