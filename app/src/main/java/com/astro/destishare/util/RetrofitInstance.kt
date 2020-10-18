package com.astro.destishare.util

import android.util.Log
import com.astro.destishare.notifications.NotificationAPI
import com.astro.destishare.util.Constants.Companion.FCM_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(FCM_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()


        }

        val notificationAPI: NotificationAPI by lazy {
            retrofit.create(
                NotificationAPI::class.java)
        }

    }
}