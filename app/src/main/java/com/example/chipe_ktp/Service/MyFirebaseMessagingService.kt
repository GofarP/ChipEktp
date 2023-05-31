package com.example.chipe_ktp.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.chipe_ktp.Activity.MainActivity
import com.example.chipe_ktp.PreferenceManager.PreferenceManager
import com.example.chipe_ktp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val CHANNEL_ID="notification_channel"
const val CHANNEL_NAME="com.example.chipe_ktp"

val database=FirebaseDatabase.getInstance().reference
val auth=FirebaseAuth.getInstance()

var  sharedPreference=PreferenceManager()

class MyFirebaseMessagingService: FirebaseMessagingService() {

    fun generateNotification(judul:String, isi:String )
    {
        val intent=Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent=PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder:NotificationCompat.Builder=NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.card)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder=builder.setContent(getRemoteView(judul, isi))

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val notificationChannel=NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }

    fun getRemoteView(title:String, message:String):RemoteViews
    {
        val remoteView=RemoteViews("com.example.chipe_ktp", R.layout.layout_notification)

        remoteView.setTextViewText(R.id.lbljudulnotifikasi,title)
        remoteView.setTextViewText(R.id.lblisinotifikasi,message)
        remoteView.setImageViewResource(R.id.ivnotification,R.drawable.card)

        return remoteView
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.notification != null)
        {
            generateNotification(message.notification!!.title.toString(), message.notification!!.body.toString())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        sharedPreference.preferenceManager(this)
        sharedPreference.putString("FCM_TOKEN",token)

        Log.d("cloud messaging:",token)
    }
}