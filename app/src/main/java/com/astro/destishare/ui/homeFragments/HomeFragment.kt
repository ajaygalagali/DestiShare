package com.astro.destishare.ui.homeFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.astro.destishare.MainActivity
import com.astro.destishare.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {

    private val TAG = "HomeFragment"

    lateinit var auth : FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()


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

    
}