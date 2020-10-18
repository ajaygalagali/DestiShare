package com.astro.destishare.notifications


data class NotificationData(
    val title: String,
    val message : String,
    val senderUID : String,
    val isAction : Boolean
)