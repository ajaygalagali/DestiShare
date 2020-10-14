package com.astro.destishare.ui.signupFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.astro.destishare.R
import com.astro.destishare.firestore.UsersData
import com.astro.destishare.ui.HomeActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private val TAG = "RegistrationFragment"
    lateinit var auth : FirebaseAuth
    lateinit var phone : String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore.collection("users")
        auth = FirebaseAuth.getInstance()
        phone = auth.currentUser?.phoneNumber.toString()
        Log.d("TAG", "Current User -> ${auth.currentUser}")
        Log.d("TAG", "Current User.Displayname -> ${auth.currentUser?.displayName}")
        Log.d("TAG", "Current User.PhoneNumber -> ${auth.currentUser?.phoneNumber}")
        Log.d("TAG", "Current User.Email -> ${auth.currentUser?.email}")



        //Get Started
        btnGetStarted.setOnClickListener {

            val fullName = etFullName.text.toString()
            val email = etEmailReg.text.toString()
            val password = etPasswordReg.text.toString()

            if (fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){

                showProgressBarOne()
                hideLayout()

                var newUser = UsersData(fullName,email,phone!!)

                // Adding user details to Firestore
                CoroutineScope(Dispatchers.IO).launch {

                    try {

                        db.add(newUser).addOnCompleteListener { task->

                            if (task.isSuccessful){
                                Log.d(TAG, "onViewCreated: Uploading user data to firestore successful")

                                createUserEmailPassword(email,password)


                            }else{
                                Log.d(TAG, "onViewCreated: Uploading user data FAILDED -> ${task.exception.toString()}")

                                hideProgressBarOne()
                                showLayout()

                                Snackbar.make(requireView(),"Something went wrong...",Snackbar.LENGTH_SHORT).show()
                            }

                        }

                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                }

            }

        }

    }


    private fun createUserEmailPassword(email : String,password : String){

        if (auth.currentUser!=null){

            auth.signOut()

        }

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->

                if (task.isSuccessful){

                    Log.d(TAG, "createUserEmailPassword: SUCCESS")
                    Intent(requireContext(), HomeActivity::class.java)
//                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .also {
                            startActivity(it)
                        }


                }else{
                    hideProgressBarOne()
                    showLayout()
                    Log.d(TAG, "createUserEmailPassword: FAILDED")
                    Snackbar.make(requireView(),"Registration Failed",Snackbar.LENGTH_SHORT).show()
                }

            }


    }




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