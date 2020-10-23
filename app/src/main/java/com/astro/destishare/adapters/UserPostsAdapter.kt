package com.astro.destishare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.astro.destishare.R
import com.astro.destishare.firestore.postsmodels.PostsModel
import kotlinx.android.synthetic.main.user_post_block.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*


class UserPostsAdapter : RecyclerView.Adapter<UserPostsAdapter.UserPostsViewHolder>() {


    inner class UserPostsViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
    val prettyTime = PrettyTime()

    private val differCallBack = object : DiffUtil.ItemCallback<PostsModel>(){
        override fun areItemsTheSame(oldItem: PostsModel, newItem: PostsModel): Boolean {
            return oldItem.id== newItem.id
        }

        override fun areContentsTheSame(oldItem: PostsModel, newItem: PostsModel): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this,differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPostsViewHolder {
        return UserPostsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.user_post_block,
                    parent,
                    false
                )

        )
    }

    override fun onBindViewHolder(holder: UserPostsViewHolder, position: Int) {
        var currentItem = differ.currentList[position]

        holder.itemView.apply {

            tvFromPost.text = currentItem.startingPoint
            tvNotePost.text = currentItem.note
            tvPeoplePost.text = currentItem.peopleCount.toString()
            tvToPost.text = currentItem.destination

            val timeStamp = prettyTime.format(currentItem.timeStamp)

            tvTimeStamp.text = timeStamp

            // Formatting Date and Time
            val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            tvDatePost.text = dateFormat.format(currentItem.deadTime)
            tvTimePost.text = timeFormat.format(currentItem.deadTime)

            tvViewOnMap.setOnClickListener {
                onViewMapClickListener?.let { it(currentItem) }
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    // View on map
    private var onViewMapClickListener:((PostsModel)->Unit)? = null

    fun setOnViewMapClickListener(listener: (PostsModel) -> Unit){
        onViewMapClickListener = listener
    }






}