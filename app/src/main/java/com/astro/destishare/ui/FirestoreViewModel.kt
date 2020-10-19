package com.astro.destishare.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.astro.destishare.firestore.UsersData
import com.astro.destishare.firestore.postsmodels.PostsModel
import com.astro.destishare.repositories.FirestoreRepository
import com.google.firebase.firestore.Query
import java.util.*

class FirestoreViewModel : ViewModel() {

    var firestoreRepository = FirestoreRepository()
    var postsFromDB : MutableLiveData<List<PostsModel>> = MutableLiveData()
    var userPostsActive : MutableLiveData<List<PostsModel>> = MutableLiveData()
    var userPostsDone : MutableLiveData<List<PostsModel>> = MutableLiveData()
    private  val TAG = "FirestoreViewModel"

    fun getAllPosts() : LiveData<List<PostsModel>>{

        firestoreRepository.getAllPosts()
            .whereGreaterThan("deadTime",Calendar.getInstance().time)
            .orderBy("deadTime")
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

    fun getUserActivePosts(uuid : String) : LiveData<List<PostsModel>>{

        firestoreRepository.getAllPosts()
            .whereGreaterThan("deadTime",Calendar.getInstance().time)
            .whereEqualTo("userID",uuid)
//            .orderBy("deadTime")
            .addSnapshotListener { value, er ->

                if (er != null){
                    Log.d(TAG, "getAllPosts: ACTIVE POSTS ->  Failed to listen to firestore")
                    postsFromDB.value = null
                    return@addSnapshotListener
                }

                var userPostsListActive : MutableList<PostsModel> = mutableListOf()

                for( doc in value!!){

                    var userPostActiveItem = doc.toObject(PostsModel::class.java)
                    userPostsListActive.add(userPostActiveItem)
                }

                userPostsActive.value = userPostsListActive

            }
        return userPostsActive

    }

    fun getUserDonePosts(uuid : String) : LiveData<List<PostsModel>>{

        firestoreRepository.getAllPosts()
            .whereLessThan("deadTime",Calendar.getInstance().time)
            .whereEqualTo("userID",uuid)
//            .orderBy("deadTime")
            .addSnapshotListener { value, er ->

                if (er != null){
                    Log.d(TAG, "getAllPosts: DONE POSTS-> Failed to listen to firestore")
                    postsFromDB.value = null
                    return@addSnapshotListener
                }

                var postsList : MutableList<PostsModel> = mutableListOf()

                for( doc in value!!){

                    var postItem = doc.toObject(PostsModel::class.java)
                    postsList.add(postItem)
                }

                userPostsDone.value = postsList

            }
        return userPostsDone

    }




}