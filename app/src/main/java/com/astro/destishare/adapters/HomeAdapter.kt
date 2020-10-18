package com.astro.destishare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.astro.destishare.R
import com.astro.destishare.firestore.postsmodels.PostsModel
import kotlinx.android.synthetic.main.post_block.view.*
import org.ocpsoft.prettytime.PrettyTime

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {


    inner class HomeViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.post_block,
                    parent,
                    false
                )

        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        var currentItem = differ.currentList[position]

        holder.itemView.apply {

            tvFromPost.text = currentItem.startingPoint
            tvNotePost.text = currentItem.note
            tvPeoplePost.text = currentItem.peopleCount.toString()
            tvTimePost.text = currentItem.deadTime
            tvToPost.text = currentItem.destination
            tvTitlePost.text = currentItem.userName + "'s plans"

            val timeStamp = prettyTime.format(currentItem.timeStamp)

            tvTimeStamp.text = timeStamp

            btnJoinPost.setOnClickListener {
                onJoinClickListener?.let { it(currentItem) }

            }


        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onJoinClickListener:((PostsModel)->Unit)? = null

    fun setOnJoinClickListener(listener:(PostsModel) -> Unit){
        onJoinClickListener = listener
    }
}