package com.astro.destishare.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.astro.destishare.repositories.LocalRepository
import com.astro.destishare.ui.viewmodels.NotificationViewModel

class NotificationVMFactory(
    private val repository: LocalRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationViewModel(repository = repository) as T
    }
}