package com.example.purrfectfinder

import android.annotation.SuppressLint
import android.graphics.Outline
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrfectfinder.Fragments.FavouriteActionListener
import com.example.purrfectfinder.SerializableDataClasses.Advertisement

class AdvertisementAdapter(
    private var mAdvertisements: List<Advertisement>,
    private var allFavs: List<Int>?,
    private val listener: FavouriteActionListener
) : RecyclerView.Adapter<AdvertisementAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.tvAdName)
        val priceTextView = itemView.findViewById<TextView>(R.id.tvAdPrice)
        val pictureImageView = itemView.findViewById<ImageView>(R.id.ivAdPicture)
        val isFavButton = itemView.findViewById<android.widget.Button>(R.id.isFav)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.ad_item, parent, false)

        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val advertisement: Advertisement = mAdvertisements[position]

        if (allFavs != null) {
            if (allFavs!!.contains(advertisement.id)) {
                // Если да, устанавливаем активную иконку
                viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_active)
            } else {
                // Если нет, устанавливаем неактивную иконку
                viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_inactive)
            }
        }

        viewHolder.nameTextView.text = advertisement.name
        viewHolder.priceTextView.text = formatPrice(advertisement.price.toString(), " ₽")

        Glide.with(viewHolder.itemView.context)
            .load(advertisement.picture) // Загрузка изображения по ссылке
            .into(viewHolder.pictureImageView) // Установка изображения в ImageView


        viewHolder.isFavButton.setOnClickListener {
            if (allFavs != null) {
                if (allFavs!!.contains(advertisement.id)) {
                    // Если да, устанавливаем активную иконку
                    listener.onRemoveFromFavourites(advertisement.id!!, viewHolder)
                } else {
                    // Если нет, устанавливаем неактивную иконку
                    listener.onAddToFavourites(advertisement.id!!, viewHolder)
                }
                allFavs = MainActivity.allFavs

                Log.e("current advertisement", advertisement.id.toString())
                Log.e("AllFavs", allFavs.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return mAdvertisements.size
    }

    fun formatPrice(price: String, currency: String): String {
        var priceElemIndex = price.length - 1
        var charCounter = 0
        var formattedPrice = currency

        while (priceElemIndex >= 0) {
            formattedPrice = price[priceElemIndex] + formattedPrice

            charCounter++
            if (price[priceElemIndex] == '.') {
                charCounter = 0
            }

            priceElemIndex--

            if (charCounter == 3) {
                charCounter = 0
                formattedPrice = " $formattedPrice" // добавляем пробел
            }
        }

        return formattedPrice
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newAds: List<Advertisement>, newFavAds: List<Int>?) {
        mAdvertisements = newAds
        allFavs = newFavAds
        notifyDataSetChanged()
    }
}