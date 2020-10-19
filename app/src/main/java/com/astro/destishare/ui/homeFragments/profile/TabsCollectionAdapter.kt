package com.astro.destishare.ui.homeFragments.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabsCollectionAdapter(fragment : Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> ActivePostFragment()
            1-> DonePostFragment()
            else-> JoinedFragment()

        }
    }
}