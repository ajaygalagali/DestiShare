package com.astro.destishare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.astro.destishare.R
import com.astro.destishare.models.NotificationModel
import kotlinx.android.synthetic.main.notification_block.view.*
import org.ocpsoft.prettytime.PrettyTime

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    private val differCallBack = object : DiffUtil.ItemCallback<NotificationModel>(){
        override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem.id== newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.notification_block,
                    parent,
                    false)
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.itemView.apply {

            tvActionNotification.text = currentItem.title
            tvDetailsNotification.text = currentItem.details

            if (currentItem.phone  != "-1"){
                tvPhoneNotification.text = currentItem.phone
            }else{
                tvPhoneNotification.visibility = View.GONE
                ivPhoneNotification.visibility = View.GONE
            }

            tvTimestampNotification.text = PrettyTime().format(currentItem.timeStamp)

        }

    }

    override fun getItemCount(): Int = differ.currentList.size
}