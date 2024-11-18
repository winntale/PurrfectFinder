package com.example.purrfectfinder.interfaces

import com.example.purrfectfinder.Adapters.AdvertisementAdapter

interface FavouriteActionListener {
    fun onAddToFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder, currentAdapter: AdvertisementAdapter)
    fun onRemoveFromFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder, currentAdapter: AdvertisementAdapter)
}