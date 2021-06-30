package com.example.finalscreenprojector.logic.notification

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import com.example.finalscreenprojector.MainActivity
import com.example.finalscreenprojector.logic.NetworkThread
import com.example.finalscreenprojector.logic.NotificationNetworkThread

class NotificationListener : NotificationListenerService() {
    companion object {
        @JvmStatic
        lateinit var mainActivity: MainActivity
    }
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val sharedPref: SharedPreferences = application.applicationContext.getSharedPreferences(
                "cps", Context.MODE_PRIVATE)
        val packageName: String = sbn?.packageName ?: ""
            MainActivity.updateTextView(packageName, mainActivity)
            val sender = Sender().execute(packageName)
    }

    class Sender : AsyncTask<String, Void, Unit>() {
        override fun doInBackground(vararg p0: String?) {
            NotificationNetworkThread.createUDPSenderForString(p0[0])?.run()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onListenerConnected() {
        super.onListenerConnected()
    }
}