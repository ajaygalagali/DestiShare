package com.astro.destishare.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "tbl_notifications")
data class NotificationModel(
    @PrimaryKey(autoGenerate = true)
    val id : Int? =null,
    val title : String,
    val details : String,
    val phone : String,
    val timeStamp : Date

)