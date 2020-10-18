package com.astro.destishare

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.astro.destishare.notifications.FirebaseService
import com.astro.destishare.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()



        if (auth.currentUser != null)
        {
            Intent(this, HomeActivity::class.java)
//                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .also {
                    startActivity(it)
                }
        }else{
            clLoadingMainActivity.visibility = View.GONE
        }

        Log.d("TAG", "Current User -> ${auth.currentUser}")
        Log.d("TAG", "Current User.Displayname -> ${auth.currentUser?.displayName}")
        Log.d("TAG", "Current User.PhoneNumber -> ${auth.currentUser?.phoneNumber}")



    }
}