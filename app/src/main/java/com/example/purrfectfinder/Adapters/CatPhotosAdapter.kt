package com.example.purrfectfinder.Adapters

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrfectfinder.R

class CatPhotosAdapter(
    private var mPhotos: List<Uri>,
) : RecyclerView.Adapter<CatPhotosAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val catphoto = itemView.findViewById<ImageView>(R.id.ivCatPhoto)
        val deletePhoto = itemView.findViewById<ImageButton>(R.id.btnDeletePhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatPhotosAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.catphoto_item, parent, false)

        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: CatPhotosAdapter.ViewHolder, position: Int) {
        val currentPhoto = mPhotos[position]

        Glide.with(viewHolder.itemView.context)
            .load(currentPhoto)
            .into(viewHolder.catphoto)

        viewHolder.deletePhoto.setOnClickListener {
            val mutatedPhotos = mPhotos.toMutableList()
            mutatedPhotos.removeAt(position)
            val newMPhotos = mutatedPhotos.toList()
            this.updatePhotos(newMPhotos)
        }
    }

    override fun getItemCount(): Int {
        return mPhotos.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updatePhotos(newPhotos: List<Uri>) {
        mPhotos = newPhotos
        notifyDataSetChanged()
    }

}