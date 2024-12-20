package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrfectfinder.Adapters.AdvertisementAdapter
import com.example.purrfectfinder.DataModel
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.GridSpacingItemDecoration
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.databinding.FragmentAdvertisementsBinding
import com.example.purrfectfinder.interfaces.FavouriteActionListener
import com.example.purrfectfinder.interfaces.TitleProvider
import kotlinx.coroutines.launch


class AdvertisementsFragment : Fragment(), FavouriteActionListener, TitleProvider {
    private val dataModel: DataModel by activityViewModels()

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

        binding.adSwitcher.displayedChild = 0
        dataModel.isAdsLoaded.value = false
        binding.tvAdsFound.visibility = GONE

        lifecycleScope.launch {
            allFavs = DbHelper.getInstance().getAllFavAds(MainActivity.currentUserId!!)

            // Инициализируем RecyclerView и адаптер
            adAdapter = AdvertisementAdapter(emptyList(), allFavs, newInstance()) { adSellerId, adPic, adName, adPrice ->
                with(activity as? MainActivity) {
                    this?.setFragment(R.id.fragmentLayout, AdCardFragment.newInstance(), listOf(adSellerId, adPic, adName, adPrice), false, true)
                }
            }
            binding.rvAds.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = adAdapter
                addItemDecoration(GridSpacingItemDecoration(2, 25, false))
            }

            try {
                data = DbHelper.getInstance().getAllData<Advertisement>("Advertisements")
                adAdapter.updateData(data, allFavs)
                binding.tvAdsFound.text = "Найдено объявлений: " + adAdapter.itemCount.toString()
            } catch (e: Exception) {
                Log.e("Error", "Failed to load data: ${e.message}")
            } finally {
                binding.adSwitcher.displayedChild = 1
                binding.tvAdsFound.visibility = VISIBLE
                dataModel.isAdsLoaded.value = true
            }
        }

        binding.rvAds.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Получаем высоту RecyclerView и текущую высоту прокрутки
                val recyclerViewHeight = recyclerView.computeVerticalScrollExtent()
                // Получаем полную высоту содержимого
                val totalHeight = recyclerView.computeVerticalScrollRange()

                // Проверяем, если прокрутка достигла конца
                val isScrolledToBottom = recyclerView.computeVerticalScrollOffset() + recyclerViewHeight >= totalHeight - 150
                Log.e("крутки", listOf(recyclerView.computeVerticalScrollOffset(), recyclerViewHeight, totalHeight).toString())
                // Если прокрутка дошла до конца, показываем кнопку
                if (isScrolledToBottom) {
                    (activity as MainActivity).showPlusButton(true)
                } else {
                    (activity as MainActivity).showPlusButton(false)
                }
            }
        })
    }

    override fun onAddToFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder, currentAdapter: AdvertisementAdapter) {
        lifecycleScope.launch {
            val userId = MainActivity.currentUserId!!  // Метод получения текущего userId
            DbHelper.getInstance().insertFavourite(userId, advertisementId)
            allFavs = DbHelper.getInstance().getAllFavAds(userId)
            currentAdapter.updateData(data, allFavs)
            viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_active)

        }
    }

    override fun onRemoveFromFavourites(advertisementId: Int, viewHolder: AdvertisementAdapter.ViewHolder, currentAdapter: AdvertisementAdapter) {
        lifecycleScope.launch {
            val userId = MainActivity.currentUserId!!  // Метод получения текущего userId
            DbHelper.getInstance().deleteFavourite(userId, advertisementId)
            allFavs = DbHelper.getInstance().getAllFavAds(userId)
            currentAdapter.updateData(data, allFavs)
            viewHolder.isFavButton.setBackgroundResource(R.drawable.ic_fav_icon_inactive)
        }
    }

    override fun getTitle(): String {
        return "Объявления"
    }

    companion object {
        var data: List<Advertisement> = emptyList()
        var allFavs: List<Int> = emptyList()
        fun newInstance() = AdvertisementsFragment()
    }
}

