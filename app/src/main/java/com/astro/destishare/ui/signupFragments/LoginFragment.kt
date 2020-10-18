package com.astro.destishare.ui.signupFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.astro.destishare.R
import com.astro.destishare.ui.HomeActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login){

    lateinit var auth : FirebaseAuth
    private val TAG = "LoginFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()



        tvSignUp.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }

        btnLogIn.setOnClickListener {

            val email = etEmailLogIn.text.toString()
            val password =etPasswordLogin.text.toString()

            if (email.isEmpty()){
                etEmailLogIn.error = "Email is required"
            }else if (password.isEmpty() || password.length < 9){
                etPasswordLogin.error = "Password must be greater than 8 characters"
            }else{

                showProgressBarOne()
                hideLayout()

                loginUser(email,password)



            }

        }




    }

    private fun loginUser(email : String,password : String){

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task->

                if (task.isSuccessful){

                    Log.d(TAG, "loginUser: SUCCESS")

                    hideProgressBarOne()
                    showLayout()
                    Intent(requireContext(), HomeActivity::class.java)
//                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .also {
                            startActivity(it)
                        }

                }else{
                    Log.d(TAG, "loginUser: FAILEd -> ${task.exception?.message}")

                    showLayout()
                    hideProgressBarOne()

                    Snackbar.make(parentFragment?.view as View,task.exception?.message!!,Snackbar.LENGTH_SHORT).show()

                }


            }



    }


    private fun showProgressBarOne(){
        clLoadingLogIn.visibility = View.VISIBLE
    }

    private fun hideProgressBarOne(){
        clLoadingLogIn.visibility = View.INVISIBLE
    }


    private fun showLayout(){
        clLoginLayout.visibility = View.VISIBLE
    }

    private fun hideLayout(){
        clLoginLayout.visibility = View.INVISIBLE
    }
}
