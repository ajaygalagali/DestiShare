package com.astro.destishare.repositories

import com.astro.destishare.db.DestiShareDatabase
import com.astro.destishare.models.NotificationModel

class LocalRepository(
    private val db : DestiShareDatabase
) {

    suspend fun upsertNotification(item:NotificationModel) = db.getDestiShareDOA().upsert(item)
    suspend fun deleteNotification(item: NotificationModel) = db.getDestiShareDOA().delete(item)

    fun getAllNotifications() = db.getDestiShareDOA().getAllNotifications()
}