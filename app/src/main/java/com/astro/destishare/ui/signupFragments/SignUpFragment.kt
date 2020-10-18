package com.astro.destishare.ui.signupFragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.astro.destishare.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.otp_bottomsheet.*
import java.util.concurrent.TimeUnit


class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private val TAG = "SignUpFragment"
    lateinit var storedVerificationId : String
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()


//        Log.d("TAG", "Current User -> ${auth.currentUser}")
//        Log.d("TAG", "Current User.Displayname -> ${auth.currentUser?.displayName}")
//        Log.d("TAG", "Current User.PhoneNumber -> ${auth.currentUser?.phoneNumber}")
//        Log.d("TAG", "Current User.Email -> ${auth.currentUser?.email}")


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Go to Log in Fragment
        tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        // Initialization of BottomSheet
        var bottomSheetBehavior = BottomSheetBehavior.from(constraintLayoutOTPbottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        // Callbacks for PhoneVerification
        val callbacks  = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: Instant/Auto retrieval of otp")

                // Phone Number is verified.
                // SignIn New User
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                // Verification failed
                e.printStackTrace()
                Log.d(TAG, "onVerificationFailed: ${e.message}")

                hideProgressBarOne()
                showSignUpLayout()

                // Show on UI
                Snackbar.make(parentFragment?.view as View,"Phone verification failed",Snackbar.LENGTH_LONG)
                    .apply {
                        setAction("Dismiss", View.OnClickListener {
                            this.dismiss()
                        })
                    }
                    .show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                Log.d(TAG, "onCodeSent: CODE SENT")
                hideProgressBarOne()
                showSignUpLayout()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                storedVerificationId = p0
                resendToken = p1

            }
        }

        // Sending Code
        btnGetOtp.setOnClickListener {

            showProgressBarOne()
            hideSignUpLayout()

            var phoneNumber = etSignUpPhoneNumber.text.toString()
            if (phoneNumber.isNotEmpty()){
                Log.d(TAG, "Phone number is not empty -> $phoneNumber")

                Log.d(TAG, "Verifying Phone Number...")

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91$phoneNumber",
                    120,
                    TimeUnit.SECONDS,
                    requireActivity(),
                    callbacks
                )
            }
        }

        // Hiding BottomSheet on Back pressed
        ibGoBackOtpBottomSheet.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // VERIFY OTP operation initialization
        btnVerifyOTP.setOnClickListener {

            // Creating PhoneAuthCredential for Manual Verification
            var otp = etOTP.text.toString()
            if (otp.isNotEmpty()){
                showProgressBar()
                val credential = PhoneAuthProvider.getCredential(storedVerificationId,otp)
                signInWithPhoneAuthCredential(credential)
            }
        }


    }

    // Signing Up new user to Firebase
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){

        Log.d(TAG, "signInWithPhoneAuthCredential: Signing up user...")
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task->

                if (task.isSuccessful){

                    Log.d(TAG, "signInWithPhoneAuthCredential: Success")

                    // Display result
                    Snackbar.make(parentFragment?.view as View,"Successfully Signed Up",Snackbar.LENGTH_SHORT).show()
                    hideProgressBar()
                    // Navigating to Registration Fragment
                    findNavController().navigate(R.id.action_signUpFragment_to_registrationFragment)

                }else{

                    Log.d(TAG, "signInWithPhoneAuthCredential: Failed-> ${task.exception}")

                    // Checking validity of User entered OTP
                    if (task.exception is FirebaseAuthInvalidCredentialsException){
                        // Invalid OTP
                        Snackbar.make(parentFragment?.view as View,"Invalid OTP",Snackbar.LENGTH_SHORT).show()
                        hideProgressBar()
                    }
                }
            }
    }

    private fun showProgressBar(){
        progressBarOTP.visibility = View.VISIBLE
    }

    private fun hideProgressBar(){
        progressBarOTP.visibility = View.INVISIBLE

    }

    private fun showProgressBarOne(){
        clLoading.visibility = View.VISIBLE
    }

    private fun hideProgressBarOne(){
        clLoading.visibility = View.INVISIBLE
    }


    private fun showSignUpLayout(){
        clSignUp.visibility = View.VISIBLE
    }

    private fun hideSignUpLayout(){
        clSignUp.visibility = View.INVISIBLE
    }

}