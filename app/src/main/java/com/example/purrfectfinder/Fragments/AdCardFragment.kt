package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.FragmentAdCardBinding
import com.example.purrfectfinder.databinding.FragmentAdvertisementsBinding
import com.example.purrfectfinder.interfaces.TitleProvider

class AdCardFragment : Fragment(), TitleProvider {

    private var _binding: FragmentAdCardBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentAdCardBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val bundle = arguments
        Log.e("args received", arguments.toString())
        args = bundle!!.getStringArrayList("args")!!.toList()

        _binding =  FragmentAdCardBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(binding.ivAdPicture.context)
            .load(args[0]) // Загрузка изображения по ссылке
            .into(binding.ivAdPicture) // Установка изображения в ImageView

        binding.tvAdName.text = args[1]
        binding.tvAdPrice.text = args[2]
    }

    override fun getTitle(): String {
        return ""
    }

    companion object {
        fun newInstance() = AdCardFragment()
        var args: List<String> = emptyList()
    }
}