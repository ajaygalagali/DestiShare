package com.astro.destishare.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.astro.destishare.R
import com.astro.destishare.db.DestiShareDatabase
import com.astro.destishare.repositories.LocalRepository
import com.astro.destishare.util.FirestoreVMFactory
import com.astro.destishare.util.NotificationVMFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    lateinit var viewModel: FirestoreViewModel
    lateinit var notificationViewModel: NotificationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()



        val viewModelFactory = FirestoreVMFactory()
        viewModel = ViewModelProvider(this, viewModelFactory).get(FirestoreViewModel::class.java)

        val notRepo = LocalRepository(DestiShareDatabase(this))
        val notificationVMFactory = NotificationVMFactory(notRepo)
        notificationViewModel = ViewModelProvider(this,notificationVMFactory).get(NotificationViewModel::class.java)

        bottomNavigationView.setupWithNavController(navHostFragmentHome.findNavController())

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+auth.currentUser?.uid!!)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }






}