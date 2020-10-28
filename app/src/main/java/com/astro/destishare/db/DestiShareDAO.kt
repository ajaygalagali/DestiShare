package com.astro.destishare.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.astro.destishare.models.NotificationModel


@Dao
interface DestiShareDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item:NotificationModel)

    @Delete
    suspend fun delete(item:NotificationModel)

    @Query("SELECT * FROM tbl_notifications")
    fun getAllNotifications() : LiveData<List<NotificationModel>>
}