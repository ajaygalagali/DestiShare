package com.astro.destishare.models.firestore.postsmodels

import java.util.*

data class PostsModel(
    var id: String ="",
    var userID: String = "",
    var userName: String = "",
    var startingPoint: List<String> = listOf(),
    var spLatLang: LatiLongi = LatiLongi(-1.000,-1.000),
    var destination: List<String> = listOf(),
    var destLatLang: LatiLongi = LatiLongi(-1.000,-1.000),
    var note: String = "Traveler hasn't left note",
    var deadTime: Date = Date(),
    var peopleCount: Int = -1,
    var timeStamp: Date = Date(),
){
}