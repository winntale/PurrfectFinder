package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.purrfectfinder.Adapters.FiltersAdapter
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.databinding.FragmentFiltersBinding
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class FiltersFragment : Fragment() {
    private var _binding: FragmentFiltersBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentFiltersBinding must not be null")

    private lateinit var breedsAdapter: FiltersAdapter
    private lateinit var colorsAdapter: FiltersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        breedsAdapter = FiltersAdapter(emptyList())
        binding.rvBreeds.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = breedsAdapter
        }

        colorsAdapter = FiltersAdapter(emptyList())
        binding.rvColors.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = colorsAdapter
        }

        val db = DbHelper()

        showLoadingScreen(true)

        lifecycleScope.launch {
            try {
                val breeds = db.getAllFilters("Breeds")
                val colors = db.getAllFilters("Colors")
                breedsAdapter.updateData(breeds)
                colorsAdapter.updateData(colors)
            } catch (e: Exception) {
                Log.e("Error", "Failed to load data: ${e.message}")
            } finally {
                showLoadingScreen(false)
                binding.btnConfirmFilters.visibility = View.VISIBLE
            }
        }

        binding.btnConfirmFilters.setOnClickListener{
            lifecycleScope.launch {
                val allBreedCheckBox = breedsAdapter.getCheckBoxes()
                var allBreedCheckedIndexes: MutableList<Int> = mutableListOf()

                val allColorCheckBox = colorsAdapter.getCheckBoxes()
                var allColorCheckedIndexes: MutableList<Int> = mutableListOf()

                var currentFilters: MutableList<Map<String, List<Int>>> = mutableListOf()

                currentFilters.add(getFilteredAds(db, allBreedCheckBox, allBreedCheckedIndexes, "breedId", "Breeds"))
                currentFilters.add(getFilteredAds(db, allColorCheckBox, allColorCheckedIndexes, "colorId", "Colors"))

                Log.e("CURRENT FILTERS LIST MAP", currentFilters.toString())

                val selectedAds = getIntersection(currentFilters)

                val client = db.getClient()

                val resultAds = client.postgrest["Advertisements"]
                    .select(columns = Columns.list("id")) {
                        filter {
                            isIn("id", selectedAds)
                            eq("gender", "Кошка")
                            gte("age", 2)
                            lte("age", 23)
                        }
                    }.decodeList<Map<String, Int>>()
                    .mapNotNull { it["id"] }

                Log.e("RESULT ADS", resultAds.toString())
            }
        }
    }

    private suspend fun getFilteredAds(db: DbHelper, allCheckBox: List<CheckBox>, allCheckedIndexes: MutableList<Int>,
                                       filterId: String, filterName: String) : Map<String, List<Int>> {
        var selectedFilters: List<Int> = listOf()
        val emptyMutableList: MutableList<Int> = mutableListOf()

        for (i in 1..allCheckBox.size) {
            if (allCheckBox[i - 1].isChecked) {
                allCheckedIndexes.add(i)
            }
        }

        val client = db.getClient()
        selectedFilters = client.postgrest[filterName]
            .select(columns = Columns.list("id")) {
                filter {
                    if (allCheckedIndexes == emptyMutableList) {
                        null
                    }
                    else isIn("id", allCheckedIndexes)
                }
            }.decodeList<Map<String, Int>>()
            .mapNotNull { it["id"] }

        val filteredData = client.postgrest["Advertisements"]
            .select(columns = Columns.list("id")) {
                filter {
                    isIn(filterId, selectedFilters)
                }
            }.decodeList<Map<String, Int>>()
            .mapNotNull { it["id"] }

        Log.e("FILTERED DATA", filteredData.toString())


        return mapOf(filterId to filteredData)

    }

    private fun getIntersection(filters: MutableList<Map<String, List<Int>>>): List<Int> {
        // Если список фильтров пуст, возвращаем пустой список
        if (filters.isEmpty()) return emptyList()

        // Инициализируем пересечение как первый список значений
        var intersection: Set<Int>? = null

        // Проходим по каждому фильтру в списке
        for (filter in filters) {
            for ((_, valueList) in filter) {
                if (intersection == null) {
                    // Инициализируем пересечение первым списком значений
                    intersection = valueList.toSet()
                } else {
                    // Найти пересечение с текущим списком значений
                    intersection = intersection.intersect(valueList.toSet())
                }
            }
        }

        // Возвращаем пересечение в виде списка
        return intersection?.toList() ?: emptyList()
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
        fun newInstance() = FiltersFragment()
    }
}