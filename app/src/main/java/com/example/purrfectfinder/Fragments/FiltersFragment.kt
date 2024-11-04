package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.purrfectfinder.Adapters.AdvertisementAdapter
import com.example.purrfectfinder.Adapters.FiltersAdapter
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.GridSpacingItemDecoration
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.databinding.FragmentAdvertisementsBinding
import com.example.purrfectfinder.databinding.FragmentFiltersBinding
import kotlinx.coroutines.launch

class FiltersFragment : Fragment() {
    private var _binding: FragmentFiltersBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFiltersBinding must not be null")

    private lateinit var filtersAdapter: FiltersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filtersAdapter = FiltersAdapter(emptyList())
        binding.rvBreeds.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = filtersAdapter
        }

        val db = DbHelper()

        showLoadingScreen(true)

        lifecycleScope.launch {
            try {
                val breeds = db.getAllBreeds()
                filtersAdapter.updateData(breeds)
            } catch (e: Exception) {
                Log.e("Error", "Failed to load data: ${e.message}")
            } finally {
                showLoadingScreen(false)
            }
        }

        binding.btnConfirmFilters.setOnClickListener{
            lifecycleScope.launch {
                filtersAdapter
            }
        }
    }

    // Функция для показа/скрытия загрузочного экрана
    private fun showLoadingScreen(isLoading: Boolean) {
        (activity as MainActivity).showLoadingScreen(isLoading)

        if (isLoading) {
            binding.llVerifiedBreed.visibility = View.GONE
            binding.llBreed.visibility = View.GONE
            binding.rvBreeds.visibility = View.GONE
        } else {
            binding.llVerifiedBreed.visibility = View.VISIBLE
            binding.llBreed.visibility = View.VISIBLE
            binding.rvBreeds.visibility = View.VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FiltersFragment()
    }
}