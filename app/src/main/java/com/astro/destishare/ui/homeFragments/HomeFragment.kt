package com.astro.destishare.ui.homeFragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.astro.destishare.MainActivity
import com.astro.destishare.R
import com.astro.destishare.adapters.HomeAdapter
import com.astro.destishare.firestore.postsmodels.PostsModel
import com.astro.destishare.notifications.FirebaseService
import com.astro.destishare.notifications.NotificationData
import com.astro.destishare.notifications.PushNotification
import com.astro.destishare.ui.FirestoreViewModel
import com.astro.destishare.ui.HomeActivity
import com.astro.destishare.util.RetrofitInstance
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
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
    val query = db.collection("posts")
    lateinit var registration : ListenerRegistration

    lateinit var viewModel : FirestoreViewModel
    lateinit var mAdapter : HomeAdapter
    


    /*override fun onStart() {
        super.onStart()

        registration = query.addSnapshotListener { value, error ->

            if (error != null){
                Log.d(TAG, "onStart: Realtime listening failed")
                return@addSnapshotListener
            }
            if (value != null) {

                for ( i in value.documents){

                    val post  = i.toObject(PostsModel::class.java)

                    Log.d(TAG, "onStart: $post")
//                    Log.d(TAG, "onStart: ${i.data}")

                    *//*for (k in i.data){
                        Log.d(TAG, "onStart: KEY -> ${k.key} || VALUE ${k.value}")


                    }*//*


                }
            } else {
                Log.d(TAG, "Current data: null")
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        registration.remove()
        Log.d(TAG, "onDestroy: Removed reg")


    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "onViewCreated: PhoneNumber -> ${auth.currentUser?.phoneNumber}")

        setupRecyclerView()

        viewModel = (activity as HomeActivity).viewModel

        // Populating Recycler View
        viewModel.getAllPosts().observe(viewLifecycleOwner, Observer {
            mAdapter.differ.submitList(it)

        })
        



        // OnJoinClick handler
        mAdapter.setOnJoinClickListener {

            val senderName = auth.currentUser?.displayName
            val title = "$senderName wants to join you"
            val message = "${it.startingPoint} -> ${it.destination}"
            val senderUID = auth.currentUser?.uid!!
            val topic = "/topics/"+it.userID

            // Send Notification to client

            if (title.isNotEmpty() && message.isNotEmpty()){

                PushNotification(
                    NotificationData(title,message,senderUID,true),
                    topic
                ).also { pushNotification->
                    sendNotification(pushNotification)
                }

            }

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
    private fun sendNotification(notification : PushNotification)= CoroutineScope(Dispatchers.IO).launch {

        try {

            val respose = RetrofitInstance.notificationAPI.postNotification(notification)

            if (respose.isSuccessful){
                Log.d(TAG, "sendNotification: RESPONSE -> {${Gson().toJson(respose)}}")

            }else{
                Log.d(TAG, "sendNotification: ${respose.errorBody()}")

            }

        }catch (e : Exception){
            e.printStackTrace()

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