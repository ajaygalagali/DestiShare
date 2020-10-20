package com.astro.destishare.ui.homeFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.astro.destishare.MainActivity
import com.astro.destishare.R
import com.astro.destishare.adapters.HomeAdapter
import com.astro.destishare.firestore.postsmodels.PostsModel
import com.astro.destishare.notifications.NotificationData
import com.astro.destishare.notifications.PushNotification
import com.astro.destishare.ui.FirestoreViewModel
import com.astro.destishare.ui.HomeActivity
import com.astro.destishare.util.RetrofitInstance
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG = "HomeFragment"

    lateinit var auth : FirebaseAuth
    val db = Firebase.firestore

    lateinit var viewModel : FirestoreViewModel
    lateinit var mAdapter : HomeAdapter
    lateinit var joinedPostsRef : CollectionReference
    

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        joinedPostsRef = db.collection("user-joined").document(auth.currentUser?.uid!!).collection("joinedPosts")


        setupRecyclerView()

        viewModel = (activity as HomeActivity).viewModel

        // Populating Recycler View
        viewModel.getAllPosts().observe(viewLifecycleOwner, Observer {
            mAdapter.differ.submitList(it)

        })

        // getting JoinedPosts
        viewModel.getJoinedPosts(auth.currentUser?.uid!!).observe(viewLifecycleOwner,Observer{

//            mAdapter.joinedDiffer.submitList(it)

            viewModel.getJoinedPostsIDs().observe(viewLifecycleOwner,Observer{
                mAdapter.joinedIDs = it
            })

        })








        // OnJoinClick handler
        mAdapter.setOnJoinClickListener {thisPost->

            val senderName = auth.currentUser?.displayName
            val title = "$senderName wants to join you"
            val message = "${thisPost.startingPoint} -> ${thisPost.destination}"
            val senderUID = auth.currentUser?.uid!!
            val topic = "/topics/"+thisPost.userID

            // Send Notification to client
            // Adding this post to user-joined collection
            if (title.isNotEmpty() && message.isNotEmpty()){

                PushNotification(
                    NotificationData(title,message,senderUID,true),
                    topic
                ).also { pushNotification->
                    sendNotification(pushNotification,thisPost)
                }

            }

            // Remove this post from feed
            // How?


        }




        // Handling Menu
        toolbar_home.setOnMenuItemClickListener { menuItem->
            if (menuItem.itemId == R.id.logout_home_menu){
                // Logout user
                try {
                    // "/topics/"+auth.currentUser?.uid
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(auth.currentUser?.uid!!).addOnCompleteListener {task->

                        if (task.isSuccessful){
                            auth.signOut()

                            Log.d(TAG, "onViewCreated: Unsubscribing SUCCESS")
                            Intent(requireActivity(),MainActivity::class.java).also {
                                startActivity(it)
                            }
                        }else{
                            
                            Snackbar.make(parentFragment?.view as View,"Something went wrong!",Snackbar.LENGTH_SHORT).show()
                            Log.d(TAG, "onViewCreated: Unsubscribing FAILED -> ${task.exception?.message}")
                            
                            
                        }
                        
                        
                        
                    }
                    
                    
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
            return@setOnMenuItemClickListener true
        }



    }

    @SuppressLint("LogNotTimber")
    private fun sendNotification(notification : PushNotification, post : PostsModel)= CoroutineScope(Dispatchers.IO).launch {

        try {

            val respose = RetrofitInstance.notificationAPI.postNotification(notification)

            if (respose.isSuccessful){
//                Log.d(TAG, "sendNotification: RESPONSE -> {${Gson().toJson(respose)}}")
                Log.d(TAG, "sendNotification: Sent notification")


                /*
                Add this post to JoinedPosts Firebase sub-collections
                */
                joinedPostsRef.add(post)
                    .addOnSuccessListener {
                        Log.d(TAG, "sendNotification: Added this post to firestore")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "sendNotification: Failed to add this post to firestore -> ${it.message}")
                    }



            }else{
                Log.d(TAG, "sendNotification: ${respose.errorBody()}")

            }

        }catch (e : Exception){
            e.printStackTrace()
            Log.d(TAG, "sendNotification: FAILED -> ${e.message}")

        }


    }

    private fun setupRecyclerView(){
        mAdapter = HomeAdapter()

        rvHomeFragment.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    
}