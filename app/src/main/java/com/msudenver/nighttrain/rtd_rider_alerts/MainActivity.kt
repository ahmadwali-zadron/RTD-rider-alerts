package com.msudenver.nighttrain.rtd_rider_alerts

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.msudenver.nighttrain.rtd_rider_alerts.ui.FavoriteStationFragment
import com.msudenver.nighttrain.rtd_rider_alerts.ui.InterfaceViewModel
import com.msudenver.nighttrain.rtd_rider_alerts.ui.ScheduleFragment
import java.time.ZoneId
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = PendingIntent.getService(applicationContext,1, Intent(this, RiderAlertService::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager?.cancel(pendingIntent)
        alarmManager?.setRepeating(AlarmManager.RTC_WAKEUP,getTomorrow6am(), AlarmManager.INTERVAL_DAY,pendingIntent)

        val uiViewModel = ViewModelProvider(this).get(InterfaceViewModel::class.java)
        uiViewModel.showStations.observe(this, Observer { showWhat -> addFragment((if (showWhat) FavoriteStationFragment() else ScheduleFragment())) })
    }

    fun getTomorrow6am() : Long {
        val cal = Calendar.getInstance()
        cal.timeZone = TimeZone.getTimeZone("MST")
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+1)
        cal.set(Calendar.HOUR_OF_DAY, 5)
        cal.set(Calendar.MINUTE, Random().nextInt(60))
        cal.set(Calendar.SECOND, Random().nextInt(60))
        return cal.time.time
    }

    @VisibleForTesting(otherwise=VisibleForTesting.PRIVATE)
    fun addFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container_view, fragment)
        fragmentTransaction.commitNow()
    }
}