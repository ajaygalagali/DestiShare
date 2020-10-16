package com.astro.destishare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FirestoreVMFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FirestoreViewModel() as T
    }
}