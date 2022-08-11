package com.example.weatherlicious.util

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.floor
import kotlin.math.roundToInt


class SignalStrengthStateListener(context: Context): LiveData<String>() {

    private var startTime: Long? = null
    private var endTime: Long? = null
    private var fileSize: Long? = null
    private val client = OkHttpClient()

    private val poorBandwidth = 150
    private val averageBandwidth = 550
    private val goodBandwidth = 2000
    
    var bandWith = MutableLiveData<String>()

    init {
        val request = Request.Builder()
            .url("https://cdn.weatherapi.com/weather/64x64/day/116.png").build()

        startTime = System.currentTimeMillis()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, exception: IOException) {
                //Toast.makeText(context, "An error occurred measuring the internet strength: $exception", Toast.LENGTH_LONG).show()
                Log.d("ioException onFailure SignalStrengthStateListener", exception.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) throw IOException("Unexpected code: $response")

                val input = response.body!!.byteStream()

                try {
                    val bos = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)

                    while (input.read(buffer) != -1){
                        bos.write(buffer)
                    }
                    val docBuffer = bos.toByteArray()
                    fileSize = bos.size().toLong()
                }finally {
                    input.close()
                }

                endTime = System.currentTimeMillis()

                val timeTakenMills =
                    floor((endTime!! - startTime!!).toDouble()) // time taken in milliseconds

                val timeTakenInSecs = timeTakenMills / 1000 // divide by 1000 to get time in seconds

                val kilobytePerSec = (1024 / timeTakenInSecs).roundToInt()

                if (kilobytePerSec <= poorBandwidth){
                    bandWith.postValue("POOR_BANDWIDTH")
                }
                if (kilobytePerSec > poorBandwidth || kilobytePerSec <= averageBandwidth){
                    bandWith.postValue("AVERAGE_BANDWIDTH")
                }
                if (kilobytePerSec > goodBandwidth){
                    bandWith.postValue("GOOD_BANDWIDTH")
                }
            }
        })
    }
}