package com.astro.destishare.repositories

import com.astro.destishare.models.firestore.postsmodels.PostsModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreRepository  (){
    private val TAG = "FirestoreRepository"
    private val db = Firebase.firestore

    fun getAllPosts() : CollectionReference {

        return db.collection("posts")

    }

    fun getJoinedPosts() : CollectionReference{
        return db.collection("user-joined")

    }

    fun deletePost(post:PostsModel): DocumentReference {
        return db.collection("posts").document(post.id)
    }


}

