package com.astro.destishare.notifications

import com.astro.destishare.models.firestore.postsmodels.PostsModel


data class NotificationData(
    val title: String,
    val message : String,
    val senderUID : String,
    val phone : String,
    val isAction : Boolean
)