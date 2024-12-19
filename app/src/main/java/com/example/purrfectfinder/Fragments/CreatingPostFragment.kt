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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.purrfectfinder.Adapters.CatPhotosAdapter
import com.example.purrfectfinder.DataModel
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.MainActivity.Companion.currentUserId
import com.example.purrfectfinder.R
import com.example.purrfectfinder.SerializableDataClasses.Post
import com.example.purrfectfinder.databinding.FragmentCreatingPostBinding
import com.example.purrfectfinder.uriToByteArray
import kotlinx.coroutines.launch


class CreatingPostFragment : Fragment() {
    private val dataModel: DataModel by activityViewModels()

    private lateinit var getContentLauncher: ActivityResultLauncher<String>

    private var _binding: FragmentCreatingPostBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentCreatingPostBinding must not be null")

    private lateinit var catPhotosAdapter: CatPhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatingPostBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        catPhotosAdapter = CatPhotosAdapter(dataModel.postPhotoUris.value ?: emptyList())

        binding.rvPhotos.apply {
            val layout = LinearLayoutManager(context)
            layout.orientation = RecyclerView.HORIZONTAL
            layoutManager = layout
            adapter = catPhotosAdapter
        }

        getContentLauncher =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
                if (uris.isNotEmpty()) {
                    val currentUris = dataModel.postPhotoUris.value
                        ?: emptyList() //Get current URIs, default to emptyList if null
                    val newUris = currentUris + uris //Concatenate the lists
                    dataModel.postPhotoUris.value = newUris.distinct() // Remove any duplicates
                }
            }

        dataModel.postPhotoUris.observe(viewLifecycleOwner) {
            if (it != null) {
                catPhotosAdapter.updatePhotos(it)
            }
        }

        binding.btnAddPostPhoto.setOnClickListener {
            getContentLauncher.launch("image/*")
        }

        binding.btnAddPost.setOnClickListener {
            val seller = MainActivity.currentUserId!!.toLong()
            val adName = binding.etName.text.toString()
            val desc = binding.etDesc.text.toString()

            lifecycleScope.launch {
                val uploadedImageUrls = savePostsToDB()

                val createdPost = Post(
                    id = null,
                    sellerId = seller,
                    name = adName,
                    desc = desc,
                    photo = uploadedImageUrls,
                    views = 0
                )

                DbHelper.getInstance().insertPost(createdPost)
            }

            with(activity as? MainActivity) {
                this?.setFragment(R.id.profileLayout, ProfileDescriptionFragment.newInstance(), args = listOf(
                    currentUserId.toString()))
                this?.setFragment(R.id.fragmentLayout, ProfileFragment.newInstance(), args = listOf(
                    currentUserId.toString()))
            }
        }


    }

    private suspend fun savePostsToDB(): List<String> {
        val uris = dataModel.postPhotoUris.value!!

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
                    val fileName = "post_${System.currentTimeMillis()}_$index"
                    val imageByteArray = uri.uriToByteArray(activity as MainActivity)!!

                    val imageUrl = DbHelper.getInstance().uploadFile(client,"petphotos", "posts", fileName, imageByteArray)
                    uploadedImagesUrls.add(imageUrl)
                } catch (e: Exception) {
                    Toast.makeText(activity as MainActivity, "Ошибка загрузки изображений", Toast.LENGTH_SHORT).show()
                }
            }

        }

        return uploadedImagesUrls
    }

    companion object {
        fun newInstance() = CreatingPostFragment()
    }
}