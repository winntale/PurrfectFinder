package com.example.purrfectfinder.Adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Post

class PostsAdapter(
    private var mPosts: List<Post> = emptyList(),
    private val onCardClicked: () -> Unit
): RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val post =  itemView.findViewById<CardView>(R.id.post)

        val postPhoto = itemView.findViewById<ImageView>(R.id.ivPostPhoto)
        val views = itemView.findViewById<TextView>(R.id.tvViewsQuantity)
        val viewicon = itemView.findViewById<ImageView>(R.id.ivViewIcon)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.post_item, parent, false)

        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        var cardWidth = (screenWidth / 3f) - .33f
        val delta = cardWidth - cardWidth.toInt()

        cardWidth = (cardWidth.toInt() - 3*delta) * displayMetrics.density
        val aspectRatio = 212f / 133f // соотношение сторон (высота / ширина)
        Log.e("cardHeightFloat", (cardWidth * aspectRatio).toString())

        val cardHeight = (cardWidth * aspectRatio).toInt()

        contactView.findViewById<CardView>(R.id.post).apply {
            layoutParams.width = (cardWidth / displayMetrics.density).toInt()
            layoutParams.height = (cardHeight / displayMetrics.density).toInt()
        }

        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: PostsAdapter.ViewHolder, position: Int) {
        val post = mPosts[position]

        Glide.with(viewHolder.itemView.context)
            .load(post.photo.first())
            .into(viewHolder.postPhoto)

        if (post.views.toInt() != -1) {
            viewHolder.views.text = post.views.toString()
        }
        else {
            viewHolder.viewicon.visibility = GONE

            viewHolder.post.setOnClickListener {
                onCardClicked()
            }
        }


    }

    override fun getItemCount(): Int {
        return mPosts.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePhotos(newPosts: List<Post>) {
        mPosts = newPosts
        notifyDataSetChanged()
    }

}