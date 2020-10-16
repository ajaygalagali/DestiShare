package com.astro.destishare.ui.homeFragments

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
import com.astro.destishare.ui.FirestoreViewModel
import com.astro.destishare.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*


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

        setupRecyclerView()

        viewModel = (activity as HomeActivity).viewModel

        viewModel.getAllPosts().observe(viewLifecycleOwner, Observer {

            mAdapter.differ.submitList(it)


        })


        // Handling Menu
        toolbar_home.setOnMenuItemClickListener { menuItem->
            if (menuItem.itemId == R.id.logout_home_menu){
                // Signout user
                try {
                    auth.signOut()
                    Intent(requireActivity(),MainActivity::class.java).also {
                        startActivity(it)
                    }
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
            return@setOnMenuItemClickListener true
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