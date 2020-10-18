package com.astro.destishare.notifications

data class PushNotification(
    val data : NotificationData,
    val to: String
)