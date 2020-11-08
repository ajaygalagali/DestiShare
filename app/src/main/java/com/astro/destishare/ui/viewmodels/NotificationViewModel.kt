package com.astro.destishare.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astro.destishare.models.NotificationModel
import com.astro.destishare.models.firestore.postsmodels.PostsModel
import com.astro.destishare.repositories.LocalRepository
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository  : LocalRepository
) : ViewModel() {

    fun getAllNotifications() = repository.getAllNotifications()

    fun upsertNotification(item : NotificationModel) = viewModelScope.launch {
        repository.upsertNotification(item)
    }

    fun deleteNotification(item : NotificationModel) = viewModelScope.launch{
        repository.deleteNotification(item)
    }


}