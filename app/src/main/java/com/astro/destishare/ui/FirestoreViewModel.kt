package com.astro.destishare.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astro.destishare.models.firestore.postsmodels.PostsModel
import com.astro.destishare.repositories.FirestoreRepository
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import java.util.*

class FirestoreViewModel : ViewModel() {

    private var firestoreRepository = FirestoreRepository()
    var postsFromDB : MutableLiveData<List<PostsModel>> = MutableLiveData()
    var userPostsActive : MutableLiveData<List<PostsModel>> = MutableLiveData()
    var userPostsDone : MutableLiveData<List<PostsModel>> = MutableLiveData()

    var joinedPostsLiveData : MutableLiveData<List<PostsModel>> = MutableLiveData()
    var joinedPostsIDsList : MutableLiveData<List<String>> = MutableLiveData()
    private  val TAG = "FirestoreViewModel"

    fun getAllPosts(userID : String) = viewModelScope.launch{

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

            val postsList : MutableList<PostsModel> = mutableListOf()

            for( doc in value!!){
                val postItem = doc.toObject(PostsModel::class.java)

                // Not adding user's post
                if (postItem.userID != userID){
                    postsList.add(postItem)
                }
            }

            postsFromDB.value = postsList

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

}