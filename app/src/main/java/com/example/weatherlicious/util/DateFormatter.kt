package com.example.weatherlicious.util

import com.example.weatherlicious.util.Constants.DATE_AND_TIME_FORMAT
import com.example.weatherlicious.util.Constants.DATE_AND_TIME_HOUR_MIN_FORMAT
import com.example.weatherlicious.util.Constants.DATE_DAY_MONTH_YEAR_AND_TIME_HOUR_MIN
import com.example.weatherlicious.util.Constants.DATE_FORMAT
import com.example.weatherlicious.util.Constants.DATE_FORMAT_API
import com.example.weatherlicious.util.Constants.TIME_FORMAT_HOURS
import com.example.weatherlicious.util.Constants.TIME_FORMAT_HOURS_MINUTES
import com.example.weatherlicious.util.Constants.TIME_FORMAT_HOURS_MINUTES_SECONDS
import com.example.weatherlicious.util.Constants.TIME_FORMAT_MINUTES_SECONDS
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateFormatter {

    companion object{

        fun Long.millisToDateDayMonthYear(): String{
            val formatter = SimpleDateFormat(DATE_FORMAT)
            return formatter.format(this)
        }

        fun Long.millisToDateYearMonthDay(): String{
            val formatter = SimpleDateFormat(DATE_FORMAT_API)
            return formatter.format(this)
        }

        fun String.dateYearMonthDayToMillis(): Long{
            val formatter = SimpleDateFormat(DATE_FORMAT_API)
            return formatter.parse(this)!!.time
        }

        fun String.dateDayMonthYearToMillis(): Long{
            val formatter = SimpleDateFormat(DATE_FORMAT)
            return formatter.parse(this)!!.time
        }

        fun String.dateYearMonthDayHourMinToMillis(): Long{
            val formatter = SimpleDateFormat(DATE_AND_TIME_HOUR_MIN_FORMAT)
            return formatter.parse(this)!!.time
        }

        fun Long.millisToDateDayMonthYearHourMinSec(): String{
            val formatter = SimpleDateFormat(DATE_AND_TIME_FORMAT)
            return formatter.format(this)
        }

        fun Long.millisToDateDayMonthYearHourMin(): String{
            val formatter = SimpleDateFormat(DATE_DAY_MONTH_YEAR_AND_TIME_HOUR_MIN)
            return formatter.format(this)
        }

        fun Long.millisToHourMinSec(): String{
            val formatter = SimpleDateFormat(TIME_FORMAT_HOURS_MINUTES_SECONDS)
            return formatter.format(this)
        }

        fun Long.millisToHourMin(): String{
            val formatter = SimpleDateFormat(TIME_FORMAT_HOURS_MINUTES)
            return formatter.format(this)
        }

        fun Long.millisToHour(): String{
            val formatter = SimpleDateFormat(TIME_FORMAT_HOURS)
            return formatter.format(this)
        }

        fun Long.millisToMinSec(): String{
            val formatter = SimpleDateFormat(TIME_FORMAT_MINUTES_SECONDS)
            return formatter.format(this)
        }

        fun Long.millisToDayOfTheWeek(): String{
            val formatter = SimpleDateFormat("EEEE")
            return formatter.format(this)
        }
    }
}