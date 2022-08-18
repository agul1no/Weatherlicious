package com.example.weatherlicious.util.dialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.weatherlicious.MainActivityViewModel
import com.example.weatherlicious.R

class DeleteOrMakeMainLocationDialog(
    private val context: Context,
    private val mainActivityViewModel: MainActivityViewModel
) {

    fun createDeleteOrMakeMainLocationDialog(){
        val deleteOrMakeMainLocationDialog = Dialog(context, R.style.Theme_Dialog)
        deleteOrMakeMainLocationDialog.setCancelable(false)
        deleteOrMakeMainLocationDialog.setTitle("Select an option")
        try {
            deleteOrMakeMainLocationDialog.setContentView(R.layout.delete_dialog)
        }catch (e: Exception){
            Log.d("DeleteOrMakeMainLocationDialog", e.toString())
        }

        val makeMainLocationButton = deleteOrMakeMainLocationDialog.findViewById<Button>(R.id.buttonMakeMainLocation)
        val deleteButton = deleteOrMakeMainLocationDialog.findViewById<Button>(R.id.buttonDelete)
        val cancelButton = deleteOrMakeMainLocationDialog.findViewById<Button>(R.id.buttonCancel)
        deleteOrMakeMainLocationDialog.show()

        makeMainLocationButton.setOnClickListener {
            deleteOrMakeMainLocationDialog.dismiss()
        }

        deleteButton.setOnClickListener {
            deleteOrMakeMainLocationDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            deleteOrMakeMainLocationDialog.dismiss()
        }
    }
}