package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.purrfectfinder.Fragments.AdCardFragment.Companion
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.FragmentAdCardBinding
import com.example.purrfectfinder.databinding.FragmentChatBinding
import com.example.purrfectfinder.interfaces.TitleProvider


class ChatFragment : Fragment(), TitleProvider {

    private var _binding: FragmentChatBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentChatBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(binding.ivAdPicture.context)
            .load(AdCardFragment.args[1]) // Загрузка изображения по ссылке
            .into(binding.ivAdPicture) // Установка изображения в ImageView

        val sellerId = AdCardFragment.args[0]

        // название и цена объявления
        binding.tvAdName.text = AdCardFragment.args[2]
        binding.tvAdPrice.text = AdCardFragment.args[3]
    }

    override fun getTitle(): String {
        return ""
    }

    companion object {
        fun newInstance() = ChatFragment()
    }
}