package com.example.purrfectfinder.Fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.purrfectfinder.DataModel
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Advertisement
import com.example.purrfectfinder.databinding.FragmentCreatingAdBinding
import com.example.purrfectfinder.databinding.FragmentFilteredAdvertisementsBinding
import com.example.purrfectfinder.interfaces.TitleProvider
import com.example.purrfectfinder.uriToByteArray
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class CreatingAdFragment : Fragment(), TitleProvider {
    private val dataModel: DataModel by activityViewModels()

    private lateinit var getContentLauncher: ActivityResultLauncher<String>

    private var _binding: FragmentCreatingAdBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentCreatingAdBinding must not be null")

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

        getContentLauncher = registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                // Сохранение списка изображений в вашей `DataModel` или обработка их
                dataModel.imageUris.value = uris
            }
        }

        val db = DbHelper()

        binding.btnAddPetPhoto.setOnClickListener {
            getContentLauncher.launch("image/*")
        }

        binding.btnAddAdvertisement.setOnClickListener {
            dsfij()
        }


    }

    private fun dsfij() {
        val isGiveaway = binding.cbGiveaway.isChecked
        val seller = MainActivity.currentUserId!!.toLong()
        val adName = binding.etName.text.toString()
        val price = binding.etPrice.text.toString().toDouble()
        val breedName = binding.etBreed.text.toString().toLong()
        val colorName = binding.etColor.text.toString().toLong()
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

            Log.e("createdAd", createdAd.toString())

            DbHelper().insertAdvertisement(createdAd)
        }
    }

    suspend fun saveAdvertisementToDB(): List<String> {
        val uris = dataModel.imageUris.value!!

        val uploadedImagesUrls = mutableListOf<String>()

        if (uris != emptyList<Uri>()) {
            // проверяем, что пользователь аутентифицирован
            val db = DbHelper()
            val isAuthenticated = db.isUserAuthenticated()
            if (!isAuthenticated) {
                Toast.makeText((activity as MainActivity), "Пользователь не аутентифицирован", Toast.LENGTH_SHORT).show()
                return emptyList()
            }

            // продолжаем загрузку файла
            val client = db.getClient()
            for ((index, uri) in uris.withIndex()) {
                try {
                    val fileName = "cat_${System.currentTimeMillis()}_$index"
                    val imageByteArray = uri.uriToByteArray(activity as MainActivity)!!

                    val imageUrl = db.uploadFile(client,"petphotos", fileName, imageByteArray)
                    uploadedImagesUrls.add(imageUrl)
                } catch (e: Exception) {
                    Toast.makeText(activity as MainActivity, "Ошибка загрузки изображений", Toast.LENGTH_SHORT).show()
                    Log.e("PIZDA VSEM", e.message.toString())
                }
            }

        }

        return uploadedImagesUrls
    }

    override fun getTitle(): String {
        return "Создание объявления"
    }

    companion object {
        fun newInstance() = CreatingAdFragment()
    }
}