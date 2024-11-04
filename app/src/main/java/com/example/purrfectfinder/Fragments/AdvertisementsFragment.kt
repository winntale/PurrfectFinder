package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.purrfectfinder.Adapters.AdvertisementAdapter
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.GridSpacingItemDecoration
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.databinding.FragmentAdvertisementsBinding
import kotlinx.coroutines.launch

interface FavouriteActionListener {
    fun onAddToFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder)
    fun onRemoveFromFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder)
}

class AdvertisementsFragment : Fragment(), FavouriteActionListener {

    private var _binding: FragmentAdvertisementsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentAdvertisementsBinding must not be null")

    private lateinit var adAdapter: AdvertisementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdvertisementsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируем RecyclerView и адаптер
        adAdapter = AdvertisementAdapter(emptyList(), MainActivity.allFavs, this)
        binding.rvAds.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adAdapter
            addItemDecoration(GridSpacingItemDecoration(2, 25, false))
        }

        val db = DbHelper()

        showLoadingScreen(true)

        lifecycleScope.launch {
            try {
                val data = db.getData<Advertisement>("Advertisements")
                adAdapter.updateData(data, MainActivity.allFavs)
                binding.tvAdsFound.text = "Найдено объявлений: " + adAdapter.itemCount.toString()
            } catch (e: Exception) {
                Log.e("Error", "Failed to load data: ${e.message}")
            } finally {
                showLoadingScreen(false)
            }
        }
    }

    override fun onAddToFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder) {
        lifecycleScope.launch {
            val userId = MainActivity.currentUserId!!  // Метод получения текущего userId
            DbHelper().insertFavourite(userId, advertisementId)
            MainActivity.allFavs = DbHelper().getAllFavAds(userId)
            viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_active)

        }
    }

    override fun onRemoveFromFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder) {
        lifecycleScope.launch {
            val userId = MainActivity.currentUserId!!  // Метод получения текущего userId
            DbHelper().deleteFavourite(userId, advertisementId)
            MainActivity.allFavs = DbHelper().getAllFavAds(userId)
            viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_inactive)
        }
    }

    // Функция для показа/скрытия загрузочного экрана
    private fun showLoadingScreen(isLoading: Boolean) {
        (activity as MainActivity).showLoadingScreen(isLoading)

        if (isLoading) {
            binding.tvAdsFound.visibility = View.GONE
            binding.rvAds.visibility = View.GONE
        } else {
            binding.tvAdsFound.visibility = View.VISIBLE
            binding.rvAds.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdvertisementsFragment()
    }
}

