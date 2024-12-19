package com.example.purrfectfinder.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.interfaces.TitleProvider
import com.example.purrfectfinder.databinding.FragmentProfileDescriptionBinding

class ProfileDescriptionFragment : Fragment(), TitleProvider {

    private var _binding: FragmentProfileDescriptionBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentProfileDescriptionBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvProfileName.text = "${MainActivity.currentUserSecondName} ${MainActivity.currentUserFirstName}"
        binding.tvRegDate.text = "С нами с ${MainActivity.currentUserCreatedAt}"

        if (MainActivity.currentUserPFP != null) {
            Glide.with(binding.ivPFP)
                .load(MainActivity.currentUserPFP) // Загрузка изображения по ссылке
                .into(binding.ivPFP) // Установка изображения в ImageView
        }

        binding.btnEdit.setOnClickListener {
            with(activity as? MainActivity) {
                this?.setFragment(
                    listOf(R.id.profileLayout, R.id.fragmentLayout),
                    listOf(ProfileDescHorizontalFragment.newInstance(), ProfileEditFragment.newInstance()),
                    null,
                    true
                )
            }
        }

    }

    override fun getTitle(): String {
        return "Редактировать профиль"
    }

    companion object {
        fun newInstance() = ProfileDescriptionFragment()
    }
}