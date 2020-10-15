package com.astro.destishare.firestore.postsmodels

data class PostsModel (
    val userID : String,
    val userName : String,
    val startingPoint : String,
    var spLatLang : LatiLongi = LatiLongi(-1.000,-1.000),
    val destination : String,
    var destLatLang : LatiLongi = LatiLongi(-1.000,-1.000),
    var note : String = "Traveler hasn't left note",
    val deadTime : String,
    val peopleCount : Int
)