package com.astro.destishare.ui.homeFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.astro.destishare.R
import kotlinx.android.synthetic.main.fragment_about_us.*

class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val linkedIn = "https://www.linkedin.com/in/ajaygalagali/"
        val github = "https://github.com/ajaygalagali"
        val behance = "https://www.behance.net/ajaygalagali"
        val gmail = "mailto:ajaygalagali01@gmail.com"

        // Arrow Back
        toolbarAboutUs.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_aboutUsFragment_to_homeFragment)
        }

        // LinkedIn
        ibLinkedIn.setOnClickListener {

            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(linkedIn)
                startActivity(this)
            }

        }

        // Behance
        ibBehance.setOnClickListener {

            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(behance)
                startActivity(this)
            }

        }

        // Github
        ibGithub.setOnClickListener {

            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(github)
                startActivity(this)
            }

        }

        // Gmail
        ibGmail.setOnClickListener {

            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(gmail)
                startActivity(this)
            }

        }


    }




}