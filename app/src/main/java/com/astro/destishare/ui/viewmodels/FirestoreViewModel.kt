package com.astro.destishare.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astro.destishare.models.firestore.postsmodels.PostsModel
import com.astro.destishare.repositories.FirestoreRepository
import com.google.api.LogDescriptor
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import java.util.*

class FirestoreViewModel : ViewModel() {

    var loadingState = MutableLiveData<Boolean>()
    var loadingStateSearchFragment = MutableLiveData<Boolean>()

    private var firestoreRepository = FirestoreRepository()
    var postsFromDB : MutableLiveData<List<PostsModel>> = MutableLiveData()
    var userPostsActive : MutableLiveData<List<PostsModel>> = MutableLiveData()
    var userPostsDone : MutableLiveData<List<PostsModel>> = MutableLiveData()

    var joinedPostsLiveData : MutableLiveData<List<PostsModel>> = MutableLiveData()
    var joinedPostsIDsList : MutableLiveData<List<String>> = MutableLiveData()
    private  val TAG = "FirestoreViewModel"
    var searchItemsLiveData : MutableLiveData<List<PostsModel>> = MutableLiveData()


    fun getAllPosts(userID : String) = viewModelScope.launch{

        loadingState.value = true

        firestoreRepository.getAllPosts()
            .whereGreaterThan("deadTime",Calendar.getInstance().time)
            .addSnapshotListener { value, er ->

            if (er != null){
                Log.d(TAG, "getAllPosts: Failed to listen to firestore")
                postsFromDB.value = null
                loadingState.value = false
                return@addSnapshotListener
            }

            val postsList : MutableList<PostsModel> = mutableListOf()

            for( doc in value!!){
                val postItem = doc.toObject(PostsModel::class.java)

                // Not adding user's post
                if (postItem.userID != userID){
                    postsList.add(postItem)
                }
            }

            // Sorting by timestamp
            postsList.sortByDescending {
                it.timeStamp
            }

            postsFromDB.value = postsList
            loadingState.value = false

        }

    }

    fun getUserActivePosts(uuid : String) = viewModelScope.launch{

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

                val userPostsListActive : MutableList<PostsModel> = mutableListOf()

                for( doc in value!!){

                    val userPostActiveItem = doc.toObject(PostsModel::class.java)
                    userPostsListActive.add(userPostActiveItem)
                }

                userPostsActive.value = userPostsListActive

            }
    }

    fun getUserDonePosts(uuid : String) = viewModelScope.launch{

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

                val postsList : MutableList<PostsModel> = mutableListOf()

                for( doc in value!!){

                    val postItem = doc.toObject(PostsModel::class.java)
                    postsList.add(postItem)
                }

                userPostsDone.value = postsList

            }
    }

    fun getJoinedPosts(uuid :String) = viewModelScope.launch{

        firestoreRepository.getJoinedPosts()
            .document(uuid)
            .collection("joinedPosts")
            .addSnapshotListener { value, er->

                if (er != null){
                    Log.d(TAG, "getAllPosts: Failed to listen to firestore")
                    joinedPostsLiveData.value = null
                    return@addSnapshotListener
                }

                val postsList : MutableList<PostsModel> = mutableListOf()

                for( doc in value!!){

                    val postItem = doc.toObject(PostsModel::class.java)
                    postsList.add(postItem)
                }

                joinedPostsLiveData.value = postsList

            }
    }

    fun getJoinedPostsIDs() : LiveData<List<String>>{
        val idList : MutableList<String> = mutableListOf()

        for (post in joinedPostsLiveData.value!!){

            val item = post.id
            idList.add(item)
        }

        joinedPostsIDsList.value = idList

        return joinedPostsIDsList
    }

    fun deletePost(post:PostsModel) = viewModelScope.launch{

        firestoreRepository.deletePost(post).delete().addOnSuccessListener {
            Log.d(TAG, "deletePost: Post deleted")

        }
            .addOnFailureListener {
                Log.d(TAG, "deletePost: FAILED -> ${it.message} ")
            }

    }

    fun searchPost(keyword : List<String>,destKeyword:String,userID: String) = viewModelScope.launch {

        loadingStateSearchFragment.value = true

        Log.d(TAG, "searchPost: Retreving data....")
        firestoreRepository.getAllPosts()
            .whereArrayContainsAny("startingPoint",keyword)
            .get().addOnSuccessListener {snap->
                Log.d(TAG, "searchPost: Starting point success")
                Log.d(TAG, "searchPost: Len = ${snap.size()}")

                val postsList : MutableList<PostsModel> = mutableListOf()
                for( doc in snap){
                    val postItem = doc.toObject(PostsModel::class.java)
                    Log.d(TAG, "searchPost: PostBeforeFiltering -> ${postItem.toString()}")

                    if (!postsList.contains(postItem) && postItem.destination.contains(destKeyword)
                        && (postItem.deadTime > Calendar.getInstance().time) && (postItem.userID != userID)){
                        postsList.add(postItem)
                        Log.d(TAG, "searchPost: Postitem -> ${postItem.toString()}")
                    }
                }
                searchItemsLiveData.value = postsList
                loadingStateSearchFragment.value = false
            }
            .addOnFailureListener {

                loadingStateSearchFragment.value = false

            }
    }

}