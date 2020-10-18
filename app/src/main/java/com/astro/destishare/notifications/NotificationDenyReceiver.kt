package com.astro.destishare.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.astro.destishare.util.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationDenyReceiver : BroadcastReceiver() {

    private  val TAG = "NotificationDenyReceive"
    private val auth = FirebaseAuth.getInstance()


    override fun onReceive(mContext: Context?, mIntent: Intent?) {


        // Deny clicked
        val senderUID = mIntent?.getStringExtra("senderUID")
        Log.d(TAG, "onReceive: senderId -> $senderUID")

        val senderName = auth.currentUser?.displayName
        val title = "$senderName denied your request"
        val message = "No worries ! Look for other travelers :)"
        val topic = "/topics/"+senderUID!!

        // Send Notification to client

        if (title.isNotEmpty() && message.isNotEmpty()){

            PushNotification(
                NotificationData(title,message,senderUID,false),
                topic
            ).also { pushNotification->
                sendNotification(pushNotification)
            }

        }

    }

    private fun sendNotification(notification : PushNotification)= CoroutineScope(Dispatchers.IO).launch {

        try {

            val respose = RetrofitInstance.notificationAPI.postNotification(notification)

            if (respose.isSuccessful){
                Log.d(TAG, "sendNotification: RESPONSE -> {${Gson().toJson(respose)}}")

            }else{
                Log.d(TAG, "sendNotification: ${respose.errorBody()}")

            }

        }catch (e : Exception){
            e.printStackTrace()

        }







    }
}