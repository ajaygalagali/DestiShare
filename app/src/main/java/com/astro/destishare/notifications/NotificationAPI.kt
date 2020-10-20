package com.astro.destishare.notifications

import com.astro.destishare.util.Constants.Companion.FCM_BASE_URL
import com.astro.destishare.util.Constants.Companion.FCM_CONTENT_TYPE
import com.astro.destishare.util.SecretKeys.Companion.FCM_SERVER_KEY
import com.squareup.okhttp.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface NotificationAPI {


    @Headers("Authorization: key=$FCM_SERVER_KEY","Content-Type:$FCM_CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        /*
        @Url()
        url : String = FCM_BASE_URL,
        */

        @Body
        notification : PushNotification
    ):Response<okhttp3.ResponseBody>


}