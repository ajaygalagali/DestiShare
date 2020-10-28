package com.astro.destishare.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.astro.destishare.R
import com.astro.destishare.db.DestiShareDatabase
import com.astro.destishare.models.NotificationModel
import com.astro.destishare.ui.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random


private const val CHANNEL_ID = "request_channel"

class FirebaseService : FirebaseMessagingService() {

    private  val TAG = "FirebaseService"

    override fun onMessageSent(sentMessage: String) {
        super.onMessageSent(sentMessage)

        Log.d("ajay", "onMessageSent: sentMessage -> $sentMessage  ")

    }
    lateinit var notification : Notification

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)



        val title = message.data["title"]
//        val postJson = message.data["post"]
//        Log.d(TAG, "onMessageReceived: Post -> $postJson")
//        val postModel: PostsModel = Gson().fromJson(postJson, PostsModel::class.java)

//        val msg = "${postModel.startingPoint} to ${postModel.destination} at ${postModel.deadTime}"
        val msg = message.data["message"]
//        message.data?.
        val senderUID = message.data["senderUID"]
        val isAction = message.data["isAction"].toBoolean()
        val phone = message.data["phone"]

        if (!isAction){

            val notificationDatabase = Room.databaseBuilder(
                applicationContext,
                DestiShareDatabase::class.java, "desti_share_db"
            ).build()

            CoroutineScope(Dispatchers.IO).launch {
                notificationDatabase.getDestiShareDOA().upsert(
                    NotificationModel(
                        null,
                        title!!,
                        msg!!,
                        phone!!,
                        Calendar.getInstance().time
                    )
                )
                Log.d(TAG, "onMessageReceived: Saved notification to Room")
            }

        }

        if (title != null && msg != null){
            createNotification(title, msg, senderUID!!, isAction, phone!!)

        }

//        createNotification(message.data["title"]!!,message.data["message"]!!)


    }

     private fun createNotification(
         title: String,
         message: String,
         senderUID: String,
         isAction: Boolean,
         phone: String,
     ){


         // Phone Intent on Notification Click
         val phoneuri: Uri = Uri.parse("tel:" + phone)
         val phoneIntent = Intent(Intent.ACTION_DIAL, phoneuri)




         val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification Channel Create function call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            // Creating Channel
            createNotificationChannel(notificationManager)
        }

        val notificationID = Random.nextInt()
//         intentAccept.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntentPhone = PendingIntent.getActivity(
            this,
            0,
            phoneIntent,
            FLAG_ONE_SHOT
        )



        if (isAction){

            // Accept handling
            val acceptBroadcastIntent = Intent(this, NotificationAcceptReceiver::class.java)
            acceptBroadcastIntent.putExtra("senderUID", senderUID)
            acceptBroadcastIntent.putExtra("notificationID", notificationID)
            acceptBroadcastIntent.putExtra("msg", message)

            val acceptPendingIntent = PendingIntent.getBroadcast(
                this,
                0, // RequestCode
                acceptBroadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Deny handling
            val denyBroadcastIntent = Intent(this, NotificationDenyReceiver::class.java)
            denyBroadcastIntent.putExtra("senderUID", senderUID)
            denyBroadcastIntent.putExtra("notificationID", notificationID)
            denyBroadcastIntent.putExtra("msg", message)


            val denyPendingIntent = PendingIntent.getBroadcast(
                this,
                1, // RequestCode
                denyBroadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


            // Defining notification with actions
            notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_mascot_blue)
                .addAction(R.drawable.ic_baseline_people_24, "Accept", acceptPendingIntent)
                .addAction(R.drawable.ic_dot, "Deny", denyPendingIntent)
                .setAutoCancel(true)
                .build()


        }else{

            // Defining notification without actions
            notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_mascot_blue)
                .setContentIntent(pendingIntentPhone)
                .setAutoCancel(true)
                .build()


        }


         // Notify
        notificationManager.notify(notificationID, notification)

    }


    // Notification Channel Creation
    private fun createNotificationChannel(notificationManager: NotificationManager){

        val channelName = "ImportantChannel"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "You get all DestiShare user requests on this channel. Do not unsubscribe."
        }

        notificationManager.createNotificationChannel(channel)

    }

}