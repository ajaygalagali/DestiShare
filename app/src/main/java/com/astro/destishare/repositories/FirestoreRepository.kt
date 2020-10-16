package com.astro.destishare.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreRepository  (
){
    private val TAG = "FirestoreRepository"
    private val db = Firebase.firestore

    fun getAllPosts() : CollectionReference {

        return db.collection("posts")

    }



    }

