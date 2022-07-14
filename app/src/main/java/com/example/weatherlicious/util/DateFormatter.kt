package com.example.weatherlicious.util

import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateFormatter {

    companion object{

        private const val DATE_FORMAT = "dd-MM-yyyy"
        private const val DATE_AND_TIME_FORMAT = "dd.MM.yy HH:mm:ss"
        private const val TIME_FORMAT_HOURS_MINUTES_SECONDS = "HH:mm:ss"
        private const val TIME_FORMAT_MINUTES_SECONDS = "mm:ss"

        fun Long.millisToDate(): String{
            val formatter = SimpleDateFormat(DATE_FORMAT)
            return formatter.format(this)
        }

        fun String.dateToMillis(): Long{
            val formatter = SimpleDateFormat(DATE_FORMAT)
            return formatter.parse(this)!!.time
        }

        fun Long.dateAndTimeFormatter(): String{
            val formatter = SimpleDateFormat(DATE_AND_TIME_FORMAT)
            return formatter.format(this)
        }

        fun Long.timeFormatterHourMinSec(): String{
            val formatter = SimpleDateFormat(TIME_FORMAT_HOURS_MINUTES_SECONDS)
            return formatter.format(this)
        }

        fun Long.timeFormatterMinSec(): String{
            val formatter = SimpleDateFormat(TIME_FORMAT_MINUTES_SECONDS)
            return formatter.format(this)
        }

    }
}