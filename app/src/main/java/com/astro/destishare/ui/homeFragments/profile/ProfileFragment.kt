package com.astro.destishare.ui.homeFragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.astro.destishare.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val demoCollectionAdapter = TabsCollectionAdapter(this)
        viewPagerProfile.adapter = demoCollectionAdapter

        val auth = FirebaseAuth.getInstance()
        toolbar_profileFragment.title = auth.currentUser?.displayName




        // Assigning tab title and icons
        TabLayoutMediator(tabLayoutProfile,viewPagerProfile){tab,position->
            when(position){
                0-> {
                    tab.text = "Active"
                    tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.mascot)
                }

                1->{
                    tab.text = "Done"
                    tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_done_all_24)
                }

                2->{
                    tab.text = "Joined"
                    tab.icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_group_add_24)
                }
            }
        }.attach()


    }


}




