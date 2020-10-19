package com.astro.destishare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.astro.destishare.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()


         if (auth.currentUser != null){

             // Checking If user has signed up with email but didn't verify phone number
            if(auth.currentUser?.phoneNumber!!.isEmpty()){
                clLoadingMainActivity.visibility = View.GONE

                findNavController(R.id.navHostFragment).navigate(R.id.action_registrationFragment_to_phoneVerificationFragment)
            }else{

                Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .also {
                        startActivity(it)
                    }

            }
        }else{
            clLoadingMainActivity.visibility = View.GONE
        }


    }
}