package com.astro.destishare.ui.signupFragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.astro.destishare.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_registration.*


class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private val TAG = "RegistrationFragment"
    lateinit var auth : FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore.collection("users")
        auth = FirebaseAuth.getInstance()

        // LoginClick
        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }


        //Get Started
        btnGetStarted.setOnClickListener {

            val fullName = etFullName.text.toString()
            val email = etEmailReg.text.toString()
            val password = etPasswordReg.text.toString()

            if (fullName.isEmpty()){
                etFullName.error = "What does people call you by?"
            }else if (email.isEmpty()){
                etEmailReg.error = "Enter email so that you can login next time"
            }else if (password.isEmpty() || password.length < 8){
                etPasswordReg.error = "8 characters minimum. You know it xD"
            }else{

                showProgressBarOne()
                hideLayout()

                createUserEmailPassword(email, password, fullName)

            }

        }

    }

//    Creating New User

    private fun createUserEmailPassword(email : String,password : String,fullName : String){


        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->

                if (task.isSuccessful){

                    Log.d(TAG, "createUserEmailPassword: SUCCESS")

                    // Updating Display Name
                    auth.currentUser?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(fullName).build())

                    // Navigating to PhoneNumberVerification
                    findNavController().navigate(R.id.action_registrationFragment_to_phoneVerificationFragment)



                }else{
                    hideProgressBarOne()
                    showLayout()
                    Log.d(TAG, "createUserEmailPassword: FAILED -> ${task.exception?.message}")
                    Snackbar.make(parentFragment?.view as View,task.exception?.message!!,Snackbar.LENGTH_SHORT).show()
                }

            }


    }

    /*
    * To show loading state
    * */

    private fun showProgressBarOne(){
        clLoadingReg.visibility = View.VISIBLE
    }

    private fun hideProgressBarOne(){
        clLoadingReg.visibility = View.INVISIBLE
    }


    private fun showLayout(){
        clRegistrationLayout.visibility = View.VISIBLE
    }

    private fun hideLayout(){
        clRegistrationLayout.visibility = View.INVISIBLE
    }



}