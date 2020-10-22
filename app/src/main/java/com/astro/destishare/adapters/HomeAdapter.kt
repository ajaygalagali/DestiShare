package com.astro.destishare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.astro.destishare.R
import com.astro.destishare.firestore.postsmodels.PostsModel
import kotlinx.android.synthetic.main.post_block.view.*
import org.ocpsoft.prettytime.PrettyTime
import org.ocpsoft.prettytime.TimeFormat
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeAdapter() : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>(),Filterable {


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
    val differFilter = AsyncListDiffer(this,differCallBack)

    var joinedIDs = listOf<String>()



    private val filter = object : Filter(){

        override fun performFiltering(q: CharSequence?): FilterResults {

            val filteredList = mutableListOf<PostsModel>()
            val filteredResults = FilterResults()


            if (q == null || q.isEmpty()){
                filteredList.addAll(differ.currentList)
            }else{

                for (post in differ.currentList){
                        val query = q.toString().toLowerCase()
                    if (post.startingPoint.toLowerCase().contains(query) ||
                                post.destination.toLowerCase().contains(query) ||
                                post.note.toLowerCase().contains(query)
                            ){
                        filteredList.add(post)
                    }
                }
            }

            filteredResults.values = filteredList

            return filteredResults

        }

        override fun publishResults(q: CharSequence?, results: FilterResults?) {
            val me = results?.values as MutableList<PostsModel>
            differFilter.submitList(me)

        }
    }

    override fun getFilter(): Filter {
        return filter

    }

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
        var currentItem = differFilter.currentList[position]


        holder.itemView.apply {

            tvFromPost.text = currentItem.startingPoint
            tvNotePost.text = currentItem.note
            tvPeoplePost.text = currentItem.peopleCount.toString()
            tvToPost.text = currentItem.destination
            tvTitlePost.text = currentItem.userName + "'s plans"

            val timeStamp = prettyTime.format(currentItem.timeStamp)

            tvTimeStamp.text = timeStamp

            // Formatting Date and Time
            val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            tvDatePost.text = dateFormat.format(currentItem.deadTime)
            tvTimePost.text = timeFormat.format(currentItem.deadTime)

            if (joinedIDs.contains(currentItem.id)){
                btnJoinPost.isEnabled = false
                btnJoinPost.text = "Requested"
            }

            // Join button click function
            btnJoinPost.setOnClickListener {
                onJoinClickListener?.let { it(currentItem) }
                btnJoinPost.isEnabled = false
                btnJoinPost.text = "Requested"
            }

            // View on map click function
            tvVIewOnMap.setOnClickListener {

                onViewMapClickListener?.let { it(currentItem) }

            }
            
        }

    }

    override fun getItemCount(): Int {
        return differFilter.currentList.size
    }

    private var onJoinClickListener:((PostsModel)->Unit)? = null

    fun setOnJoinClickListener(listener:(PostsModel) -> Unit){
        onJoinClickListener = listener
    }

    private var onViewMapClickListener:((PostsModel)->Unit)? = null

    fun setOnViewMapClickListener(listener: (PostsModel) -> Unit){
        onViewMapClickListener = listener
    }

}