package com.astro.destishare.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.astro.destishare.R
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val msg = intent.getStringExtra("message")
        val title = intent.getStringExtra("title")
        tvNotificationTitle.text  = title
        tvNotificationMessage.text  = msg




    }




}