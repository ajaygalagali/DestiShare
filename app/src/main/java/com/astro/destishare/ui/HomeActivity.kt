package com.astro.destishare.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.astro.destishare.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        checkCurrentUser()


        Log.d("TAG", "Current User -> ${auth.currentUser}")
        Log.d("TAG", "Current User.Displayname -> ${auth.currentUser?.displayName}")
        Log.d("TAG", "Current User.PhoneNumber -> ${auth.currentUser?.phoneNumber}")
        Log.d("TAG", "Current User.Email -> ${auth.currentUser?.email}")

        btnLogoutTest.setOnClickListener {

            auth.signOut()
            checkCurrentUser()

        }


    }

    private fun checkCurrentUser(){
        if (auth.currentUser == null){
            tvLoginStatus.text = "NULL"
        }else{
            tvLoginStatus.text = "Someone is here!"
        }
    }
}