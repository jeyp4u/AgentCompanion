package com.aanda.agent.companion

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

open class DateUtils {
    companion object {

        fun addDays(date: Date?, days: Int): String? {
            val cal: Calendar = Calendar.getInstance()
            cal.setTime(date)
            cal.add(Calendar.DATE, days) //minus number would decrement the days
            return SimpleDateFormat("yyyy-MM-dd").format(cal.time)
        }

        open fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd").format(Date())
        //   return "2020-05-14"

        }

        open fun getBeforeSheduledTIme(appointmentDate: Date): String {
            val calendar = Calendar.getInstance()
            calendar.time = appointmentDate;
            calendar.add(Calendar.MINUTE, -30)
            val currentTime = calendar.timeInMillis
            val simpleDateFormat = SimpleDateFormat("h:mm a")
            val date = Date(currentTime)
            val time = simpleDateFormat.format(date)
            return time
        }

        open fun getAppointmentDate(time: String): String {

            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time)
            val newDate = SimpleDateFormat("yyyy-MM-dd ").format(date)
            return newDate
        }

        open fun getAppointmentDateValue(time: String): Date {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time)
            return date
        }

        open fun getAppointmentTime(time: String): String {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time)
            val newTime = SimpleDateFormat("h:mm a").format(date)

            return newTime
        }

        open fun getTriggerTime(appoinmenttime: String): Calendar {

            val cal = Calendar.getInstance()
            // 2020-05-12 09:00:00
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            cal.time = sdf.parse(appoinmenttime) //

            Log.d(javaClass.name, "cal.time " + cal.time)

            return cal
        }

    }

}