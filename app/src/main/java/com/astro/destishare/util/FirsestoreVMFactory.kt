package com.astro.destishare.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.astro.destishare.ui.viewmodels.FirestoreViewModel

class FirestoreVMFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FirestoreViewModel() as T
    }
}