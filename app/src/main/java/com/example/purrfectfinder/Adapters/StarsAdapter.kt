package com.example.purrfectfinder.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.interfaces.FavouriteActionListener

class StarsAdapter(
    private var mStars: List<Float>,
) : RecyclerView.Adapter<StarsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val star = itemView.findViewById<ImageView>(R.id.ivStar)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.star_item, parent, false)

        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val currentStar = mStars[position]

        if (currentStar == 1f)
            viewHolder.star.setImageResource(R.drawable.ic_star)
        else
            viewHolder.star.setImageResource(R.drawable.ic_halfstar)
    }

    override fun getItemCount(): Int {
        return mStars.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(starRate: List<Float>) {
        mStars = starRate
        notifyDataSetChanged()
    }
}