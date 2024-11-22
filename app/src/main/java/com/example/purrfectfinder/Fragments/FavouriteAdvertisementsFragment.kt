package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.purrfectfinder.Adapters.AdvertisementAdapter
import com.example.purrfectfinder.DataModel
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.GridSpacingItemDecoration
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.databinding.FragmentFavouriteAdvertisementsBinding
import com.example.purrfectfinder.interfaces.FavouriteActionListener
import com.example.purrfectfinder.interfaces.TitleProvider
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class FavouriteAdvertisementsFragment : Fragment(), FavouriteActionListener, TitleProvider {
//    private val dataModel: DataModel by viewModels()

    private var _binding: FragmentFavouriteAdvertisementsBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFavouriteAdvertisementsBinding must not be null")

    private lateinit var adAdapter: AdvertisementAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteAdvertisementsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adAdapter = AdvertisementAdapter(emptyList(), allFavs, newInstance()) { adPic, adName, adPrice ->
            with(activity as? MainActivity) {
                this?.setFragment(R.id.fragmentLayout, AdCardFragment.newInstance(), listOf(adPic, adName, adPrice), false, true)
            }
        }
        binding.rvFavAds.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = adAdapter
            addItemDecoration(GridSpacingItemDecoration(2, 25, false))
        }


//        dataModel.favAds.observe(viewLifecycleOwner) {
//            Log.e("RECEIVED FAVOURITES", dataModel.favAds.value.toString())
//        }

//        data = dataModel.favAds.value.orEmpty()!!
//        allFavs = dataModel.favAdsIds.value!!
//
//        adAdapter.updateData(data, allFavs)

        showLoadingScreen(true)

        val db = DbHelper()

        lifecycleScope.launch {
            try {
                val client = db.getClient()

                allFavs = db.getAllFavAds(MainActivity.currentUserId!!)

                // Получаем все объявления, которые совпадают с advertisementId
                data = client.postgrest["Advertisements"]
                    .select() {
                        filter {
                            isIn("id", allFavs) // фильтруем по списку advertisementId
                        }
                    }
                    .decodeList<Advertisement>()

                adAdapter.updateData(data, allFavs)

            } catch (e: Exception) {
                Log.e("Error", "Failed to load data: ${e.message}")
            } finally {
                showLoadingScreen(false)
            }
        }

    }

    override fun onAddToFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder, currentAdapter: AdvertisementAdapter) {
        lifecycleScope.launch {
            val userId = MainActivity.currentUserId!!  // Метод получения текущего userId
            DbHelper().insertFavourite(userId, advertisementId)
            allFavs = DbHelper().getAllFavAds(userId)
            currentAdapter.updateData(data, allFavs)
            viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_active)

        }
    }

    override fun onRemoveFromFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder, currentAdapter: AdvertisementAdapter) {
        lifecycleScope.launch {
            val userId = MainActivity.currentUserId!!  // Метод получения текущего userId
            DbHelper().deleteFavourite(userId, advertisementId)
            allFavs = DbHelper().getAllFavAds(userId)
            currentAdapter.updateData(data, allFavs)
            viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_inactive)
        }
    }

    override fun getTitle(): String {
        return "Избранное"
    }

    private fun showLoadingScreen(isLoading: Boolean) {
        (activity as MainActivity).showLoadingScreen(isLoading)

        if (isLoading) {
            binding.rvFavAds.visibility = View.GONE
        } else {
            binding.rvFavAds.visibility = View.VISIBLE
        }
    }

    companion object {
        var data: List<Advertisement> = emptyList()
        var allFavs: List<Int> = emptyList()
        fun newInstance() = FavouriteAdvertisementsFragment()
    }
}