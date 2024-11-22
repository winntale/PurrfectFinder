package com.example.purrfectfinder.Adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.interfaces.FavouriteActionListener
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Advertisement

class AdvertisementAdapter(
    private var mAdvertisements: List<Advertisement>,
    private var allFavs: List<Int>,
    private val listener: FavouriteActionListener,
    private val onCardClicked: (String, String, String) -> Unit
) : RecyclerView.Adapter<AdvertisementAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val adv =  itemView.findViewById<CardView>(R.id.ad)

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

        // пустой список для сравнения
        val emptyList: List<Int> = emptyList()

        // первоначальный рендер ресайклера, основанный на актуальной информации в бд
        if (allFavs != emptyList && allFavs.contains(advertisement.id)) {
            // устанавливаем активную иконку
            viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_active)
        }
        else {
            // если нет, устанавливаем неактивную иконку
            viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_inactive)
        }

        viewHolder.nameTextView.text = advertisement.name
        viewHolder.priceTextView.text = formatPrice(advertisement.price.toString(), " ₽")

        Glide.with(viewHolder.itemView.context)
            .load(advertisement.picture) // Загрузка изображения по ссылке
            .into(viewHolder.pictureImageView) // Установка изображения в ImageView

        viewHolder.isFavButton.setOnClickListener {
            // если список не пуст и содержит айди текущего объявления
            if (allFavs != emptyList && allFavs.contains(advertisement.id)) {
                // устанавливаем активную иконку и меняем бд с помощью слушателя
                listener.onRemoveFromFavourites(advertisement.id!!, viewHolder, this)
            } else {
                // Если нет, устанавливаем неактивную иконку
                listener.onAddToFavourites(advertisement.id!!, viewHolder, this)
            }

            Log.e("current advertisement", advertisement.id.toString())
            Log.e("AllFavs", allFavs.toString())
        }

        viewHolder.adv.setOnClickListener {
            onCardClicked(advertisement.picture, advertisement.name, viewHolder.priceTextView.text.toString())
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
    fun updateData(newAds: List<Advertisement>, newFavAds: List<Int>) {
        mAdvertisements = newAds
        allFavs = newFavAds
        notifyDataSetChanged()
    }
}