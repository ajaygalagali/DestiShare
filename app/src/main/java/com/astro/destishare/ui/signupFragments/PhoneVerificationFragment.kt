package com.astro.destishare.ui.signupFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.astro.destishare.R
import com.astro.destishare.ui.HomeActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.fragment_phone_verification_fragment.*

import kotlinx.android.synthetic.main.otp_bottomsheet.*
import java.util.concurrent.TimeUnit


class PhoneVerificationFragment : Fragment(R.layout.fragment_phone_verification_fragment) {

    private val TAG = "SignUpFragment"
    lateinit var storedVerificationId : String
    lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialization of BottomSheet
        var bottomSheetBehavior = BottomSheetBehavior.from(constraintLayoutOTPbottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        // Callbacks for PhoneVerification
        val callbacks  = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted: Instant/Auto retrieval of otp")

                // Phone Number is verified.
                // SignIn New User
                addPhoneNumbertoCurrentUser(credential)
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



            var phoneNumber = etSignUpPhoneNumber.text.toString()
            if (phoneNumber.isNotEmpty()){

                showProgressBarOne()
                hideSignUpLayout()
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
                addPhoneNumbertoCurrentUser(credential)
            }
        }


    }

    // Signing Up new user to Firebase
    private fun addPhoneNumbertoCurrentUser(credential: PhoneAuthCredential){

        Log.d(TAG, "signInWithPhoneAuthCredential: Signing up user...")

        auth.currentUser?.updatePhoneNumber(credential)?.addOnSuccessListener {

            // Navigating to HomeActivity
            Intent(requireContext(),HomeActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }

        }
            ?.addOnFailureListener {

                Log.d(TAG, "addPhoneNumbertoCurrentUser: ${it.message}")

                Snackbar.make(parentFragment?.view as View, it.message.toString(),Snackbar.LENGTH_SHORT).show()

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