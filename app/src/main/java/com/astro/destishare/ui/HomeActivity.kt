package com.astro.destishare.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.astro.destishare.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var viewModel: FirestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()



        val viewModelFactory = FirestoreVMFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(FirestoreViewModel::class.java)


        bottomNavigationView.setupWithNavController(navHostFragmentHome.findNavController())

        Log.d("TAG", "Current User -> ${auth.currentUser}")
        Log.d("TAG", "Current User.Displayname -> ${auth.currentUser?.displayName}")
        Log.d("TAG", "Current User.PhoneNumber -> ${auth.currentUser?.phoneNumber}")
        Log.d("TAG", "Current User.Email -> ${auth.currentUser?.email}")




    }


}