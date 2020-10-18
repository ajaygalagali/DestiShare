package com.astro.destishare.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.astro.destishare.firestore.UsersData
import com.astro.destishare.firestore.postsmodels.PostsModel
import com.astro.destishare.repositories.FirestoreRepository
import com.google.firebase.firestore.Query

class FirestoreViewModel : ViewModel() {

    var firestoreRepository = FirestoreRepository()
    var postsFromDB : MutableLiveData<List<PostsModel>> = MutableLiveData()
    private  val TAG = "FirestoreViewModel"
    var phoneNumber : String = ""

    fun getAllPosts() : LiveData<List<PostsModel>>{

        firestoreRepository.getAllPosts()
            .orderBy("timeStamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, er ->

            if (er != null){
                Log.d(TAG, "getAllPosts: Failed to listen to firestore")
                postsFromDB.value = null
                return@addSnapshotListener
            }

            var postsList : MutableList<PostsModel> = mutableListOf()

            for( doc in value!!){

                var postItem = doc.toObject(PostsModel::class.java)
                postsList.add(postItem)
            }

            postsFromDB.value = postsList

        }
        return postsFromDB

    }


}