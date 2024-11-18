package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.purrfectfinder.Adapters.FiltersAdapter
import com.example.purrfectfinder.DataModel
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.interfaces.TitleProvider
import com.example.purrfectfinder.databinding.FragmentFiltersBinding
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlinx.coroutines.launch
import kotlin.reflect.jvm.internal.impl.util.Check

class FiltersFragment : Fragment(), TitleProvider {
    private val dataModel: DataModel by activityViewModels()

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

        // подключаем адаптеры для RecyclerView's
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

        showLoadingScreen(true)
        val db = DbHelper()

        // показываем экран загрузки

        lifecycleScope.launch {
            try {
                // получаем все названия фильтров для каждой категории
                val breeds = db.getAllFilters("Breeds")
                val colors = db.getAllFilters("Colors")
                // обновляем адаптеры с полученными данными
                breedsAdapter.updateData(breeds)
                colorsAdapter.updateData(colors)
                // в конце корутины скрываем экран загрузки, делаем видимой кнопку "Подтвердить фильтры"
                showLoadingScreen(false)
                binding.btnConfirmFilters.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("Error", "Failed to load data: ${e.message}")
            }
        }



        // обработка нажатия на кнопку "Подтвердить фильтры"
        binding.btnConfirmFilters.setOnClickListener{
            (activity as MainActivity).updateLoadingFragmentText("Применяем фильтры...")
            // показываем экран загрузки
            showLoadingScreen(true)


            lifecycleScope.launch {
                // получаем список всех элементов CheckBox для каждой из доступных категорий фильтров
                val allBreedCheckBox = breedsAdapter.getCheckBoxes()
                val allColorCheckBox = colorsAdapter.getCheckBoxes()
                // все CheckBox'ы всех категорий фильтров
                val allCheckBox : List<List<CheckBox>> = listOf(allBreedCheckBox, allColorCheckBox)

                val filterIds = listOf("breedId", "colorId")

                val filteredData = getFilteredAds(db, allCheckBox, filterIds)

                Log.e("CURRENT FILTERS LIST MAP", filteredData.toString())

                val client = db.getClient()

                val resultAds = client.postgrest["Advertisements"]
                    .select(columns = Columns.list("id")) {
                        filter {
                            isIn("id", filteredData)
//                            eq("gender", "Кошка")
//                            gte("age", 2)
//                            lte("age", 23)
                        }
                    }.decodeList<Map<String, Int>>()
                    .mapNotNull { it["id"] }

                Log.e("RESULT ADS", ArrayList(resultAds).toString())

                dataModel.filteredAds.value = resultAds

//                showLoadingScreen(false)
            }
        }
    }

    private suspend fun getFilteredAds(db: DbHelper, allCheckBox: List<List<CheckBox>>, filterIds: List<String>) : List<Int> {
        var allCheckedIndexes: MutableList<MutableList<Int>> = mutableListOf()

        for (i in 1..allCheckBox.size) {
            allCheckedIndexes.add(mutableListOf())
            for (j in 1..allCheckBox[i - 1].size) {
                if (allCheckBox[i - 1][j - 1].isChecked) {
                    allCheckedIndexes[i - 1].add(j)
                }
            }
        }

        val client = db.getClient()

        val filteredData = client.postgrest["Advertisements"]
            .select(columns = Columns.list("id")) {
                filter {
                    filterIds.forEachIndexed { index, filterId ->
                        // Проверяем, что allCheckedIndexes не пустой для текущего индекса
                        if (allCheckedIndexes[index].isNotEmpty()) {
                            isIn(filterId, allCheckedIndexes[index])
                        }
                    }
                }
            }.decodeList<Map<String, Int>>()
            .mapNotNull { it["id"] }

        Log.e("FILTERED DATA", filteredData.toString())

        return filteredData
    }

    private fun onLoadingChange(visibility: Int) {
        with(binding) {
            llVerifiedBreed.visibility = visibility
            llBreed.visibility = visibility
            rvBreeds.visibility = visibility

            llColors.visibility = visibility
            rvColors.visibility = visibility
            btnConfirmFilters.visibility = visibility
        }
    }

    override fun getTitle(): String {
        return "Фильтры"
    }

    // Функция для показа/скрытия загрузочного экрана
    private fun showLoadingScreen(isLoading: Boolean) {
        (activity as MainActivity).showLoadingScreen(isLoading)

        if (isLoading) {
            onLoadingChange(View.GONE)
        } else {
            onLoadingChange(View.VISIBLE)
        }
    }

    companion object {
        fun newInstance() = FiltersFragment()
    }
}