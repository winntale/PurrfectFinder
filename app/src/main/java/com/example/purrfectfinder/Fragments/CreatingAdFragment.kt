package com.example.purrfectfinder.Fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrfectfinder.Adapters.CatPhotosAdapter
import com.example.purrfectfinder.DataModel
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.databinding.FragmentCreatingAdBinding
import com.example.purrfectfinder.databinding.FragmentFilteredAdvertisementsBinding
import com.example.purrfectfinder.interfaces.TitleProvider
import com.example.purrfectfinder.uriToByteArray
import com.google.android.material.checkbox.MaterialCheckBox.OnCheckedStateChangedListener
import com.google.android.material.checkbox.MaterialCheckBox.STATE_CHECKED
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class CreatingAdFragment : Fragment(), TitleProvider {
    private val dataModel: DataModel by activityViewModels()

    private lateinit var getContentLauncher: ActivityResultLauncher<String>

    private var _binding: FragmentCreatingAdBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentCreatingAdBinding must not be null")

    private lateinit var catPhotosAdapter: CatPhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatingAdBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).updateLoadingFragmentText("Подготавливаем данные...")
        // показываем экран загрузки
        showLoadingScreen(true)

        lifecycleScope.launch {

            val allBreedNames = DbHelper.getInstance().getColumnFrom<String>("Breeds", "name")
            val allColorNames = DbHelper.getInstance().getColumnFrom<String>("Colors", "name")

            val breedAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                allBreedNames
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            val colorAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                allColorNames
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            binding.breedSpinner.adapter = breedAdapter
            binding.colorSpinner.adapter = colorAdapter

            showLoadingScreen(false)
            Log.e("current db", DbHelper.getInstance().toString())
            Log.e("current client", DbHelper.getInstance().getClient().toString())
        }


        catPhotosAdapter = CatPhotosAdapter(dataModel.imageUris.value ?: emptyList())

        binding.rvPhotos.apply {
            val layout = LinearLayoutManager(context)
            layout.orientation = RecyclerView.HORIZONTAL
            layoutManager = layout
            adapter = catPhotosAdapter
        }

        binding.cbGiveaway.setOnCheckedChangeListener { buttonView, isChecked ->
            if (binding.cbGiveaway.isChecked) {
                binding.tvPrice.setTextColor(
                    ContextCompat.getColor(
                        activity as MainActivity,
                        R.color.lightButtonColor
                    )
                )
            }
            else {
                binding.tvPrice.setTextColor(
                    ContextCompat.getColor(
                        activity as MainActivity,
                        R.color.textColor
                    )
                )
            }
            binding.lPrice.isEnabled = !isChecked
        }

        getContentLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                val currentUris = dataModel.imageUris.value ?: emptyList() //Get current URIs, default to emptyList if null
                val newUris = currentUris + uris //Concatenate the lists
                dataModel.imageUris.value = newUris.distinct() // Remove any duplicates
            }
        }

        binding.btnAddPetPhoto.setOnClickListener {
            getContentLauncher.launch("image/*")
        }

        dataModel.imageUris.observe(viewLifecycleOwner) {
            if (it != null) {
                catPhotosAdapter.updatePhotos(it)
            }
        }

        binding.btnAddAdvertisement.setOnClickListener {
            val seller = MainActivity.currentUserId!!.toLong()
            val adName = binding.etName.text.toString()
            val price = binding.etPrice.text.toString().toDouble()
            val breedName = binding.breedSpinner.selectedItemPosition.toLong() + 1
            val colorName = binding.colorSpinner.selectedItemPosition.toLong() + 1
            val gender =
                if (binding.radioTomcat.isChecked) "Кот"
                else "Кошка"
            val age = binding.etAgeYears.text.toString().toInt() * 12 + binding.etAgeMonths.text.toString().toInt()

            lifecycleScope.launch {
                val uploadedImageUrls = saveAdvertisementToDB()

                val createdAd = Advertisement(
                    id = null,
                    sellerId = seller,
                    name = adName,
                    price = price,
                    picture = uploadedImageUrls,
                    verifiedBreed = false,
                    breedId = breedName,
                    colorId = colorName,
                    gender = gender,
                    age = age.toLong()
                )

                DbHelper.getInstance().insertAdvertisement(createdAd)
            }
        }


    }

    suspend fun saveAdvertisementToDB(): List<String> {
        val uris = dataModel.imageUris.value!!

        val uploadedImagesUrls = mutableListOf<String>()

        if (uris != emptyList<Uri>()) {
            // проверяем, что пользователь аутентифицирован
            val isAuthenticated = DbHelper.getInstance().isUserAuthenticated()
            if (!isAuthenticated) {
                Toast.makeText((activity as MainActivity), "Пользователь не аутентифицирован", Toast.LENGTH_SHORT).show()
                return emptyList()
            }

            // продолжаем загрузку файла
            val client = DbHelper.getInstance().getClient()
            for ((index, uri) in uris.withIndex()) {
                try {
                    val fileName = "cat_${System.currentTimeMillis()}_$index"
                    val imageByteArray = uri.uriToByteArray(activity as MainActivity)!!

                    val imageUrl = DbHelper.getInstance().uploadFile(client,"petphotos", "public", fileName, imageByteArray)
                    uploadedImagesUrls.add(imageUrl)
                } catch (e: Exception) {
                    Toast.makeText(activity as MainActivity, "Ошибка загрузки изображений", Toast.LENGTH_SHORT).show()
                }
            }

        }

        return uploadedImagesUrls
    }

    private fun onLoadingChange(visibility: Int) {
        binding.clAllContent.visibility = visibility
    }

    private fun showLoadingScreen(isLoading: Boolean) {
        (activity as MainActivity).showLoadingScreen(isLoading)

        if (isLoading) {
            onLoadingChange(View.GONE)
        } else {
            onLoadingChange(View.VISIBLE)
        }
    }

    override fun getTitle(): String {
        return "Создание объявления"
    }

    companion object {
        fun newInstance() = CreatingAdFragment()
    }
}