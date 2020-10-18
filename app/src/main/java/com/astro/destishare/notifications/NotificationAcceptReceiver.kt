package com.astro.destishare.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.astro.destishare.ui.homeFragments.HomeFragment
import com.astro.destishare.util.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationAcceptReceiver : BroadcastReceiver() {

    private val TAG = "NotificationReceiver"
    private val auth = FirebaseAuth.getInstance()

    override fun onReceive(mContext: Context?, mIntent: Intent?) {

        // Accept clicked
        val senderUID = mIntent?.getStringExtra("senderUID")
        Log.d(TAG, "onReceive: senderId -> $senderUID")

        val senderName = auth.currentUser?.displayName
        val title = "$senderName accepted your request"
        val message = "Contact on this number"
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


    // Sending notification to requester
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
