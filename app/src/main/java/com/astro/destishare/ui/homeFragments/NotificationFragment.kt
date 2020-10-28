package com.astro.destishare.ui.homeFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.astro.destishare.R
import com.astro.destishare.adapters.NotificationAdapter
import com.astro.destishare.adapters.UserPostsAdapter
import com.astro.destishare.models.NotificationModel
import com.astro.destishare.ui.HomeActivity
import com.astro.destishare.ui.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_active_post.*
import kotlinx.android.synthetic.main.fragment_done_post.*
import kotlinx.android.synthetic.main.fragment_notification.*


class NotificationFragment : Fragment(R.layout.fragment_notification){

    private lateinit var mAdapter : NotificationAdapter
    private lateinit var viewModel: NotificationViewModel
    private val TAG = "NotificationFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        viewModel = (activity as HomeActivity).notificationViewModel

        viewModel.getAllNotifications().observe(viewLifecycleOwner, Observer{
            mAdapter.differ.submitList(it)
        })

        // Navigation Click
        toolbar_notifications.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_notificationFragment_to_homeFragment)
        }

//        viewModel.upsertNotification(NotificationModel(title = "This is test",details = "Details are imp",phone = "+91 45644 45644"))
//        viewModel.upsertNotification(NotificationModel(title = "This is test",details = "Details are imp",phone = "-1"))


    }

    private fun setupRecyclerView(){
        mAdapter = NotificationAdapter()

        // Swipe to delete notification
        val swipeCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val notificationItem = mAdapter.differ.currentList[viewHolder.adapterPosition]
                viewModel.deleteNotification(notificationItem)

            }
        }

        rvNotifications.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(activity)
        }

        // Attaching SwipeCallback to RecyclerView
        ItemTouchHelper(swipeCallback).attachToRecyclerView(rvNotifications)
    }


}