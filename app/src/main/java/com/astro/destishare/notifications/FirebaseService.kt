package com.astro.destishare.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.astro.destishare.R
import com.astro.destishare.ui.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "request_channel"

class FirebaseService : FirebaseMessagingService() {

    /*companion object{
        var sharedPref: SharedPreferences? = null
        var token : String?
        get() {
            return sharedPref?.getString("token","")
        }
        set(value) {
            sharedPref?.edit()?.putString("token",value)?.apply()
        }
    }*/

    /*override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)

        token = newToken


    }*/

    lateinit var notification : Notification

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)



        val title = message.data["title"]
        val msg = message.data["message"]
//        message.data?.
        val senderUID = message.data["senderUID"]
        val isAction = message.data["isAction"].toBoolean()
        Log.d("FCM", "onMessageReceived: title -> $title || msg-> $msg")

        if (title != null || msg != null){
            createNotification(title!!,msg!!,senderUID!!,isAction)

        }

//        createNotification(message.data["title"]!!,message.data["message"]!!)


    }

     fun createNotification(title : String, message : String, senderUID : String, isAction : Boolean){

        // Temp || Make it go Accept/Decline Fragment
        val intent = Intent(this,HomeActivity::class.java)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel Create function call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationID = Random.nextInt()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,FLAG_ONE_SHOT)



        if (isAction){

            // Accept handling
            val acceptBroadcastIntent = Intent(this,NotificationAcceptReceiver::class.java)
            acceptBroadcastIntent.putExtra("senderUID",senderUID)

            val acceptPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                acceptBroadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            // Deny handling
            val denyBroadcastIntent = Intent(this,NotificationDenyReceiver::class.java)
            denyBroadcastIntent.putExtra("senderUID",senderUID)

            val denyPendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                denyBroadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)


            // Defining notification with actions
            notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_mascot_blue)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_people_24,"Accept",acceptPendingIntent)
                .addAction(R.drawable.ic_dot,"Deny",denyPendingIntent)
                .setAutoCancel(true)
                .build()


        }else{

            // Defining notification without actions
            notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_mascot_blue)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()


        }



        notificationManager.notify(notificationID,notification)

    }

    private fun createNotificationChannel(notificationManager: NotificationManager){

        val channelName = "ImportantChannel"
        val channel = NotificationChannel(CHANNEL_ID,channelName,IMPORTANCE_HIGH).apply {
            description = "You get all DestiShare user requests on this channel. Do not unsubscribe."
        }

        notificationManager.createNotificationChannel(channel)

    }

}