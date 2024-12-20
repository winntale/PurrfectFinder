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
import com.example.purrfectfinder.databinding.FragmentProfileDescHorizontalBinding

class ProfileDescHorizontalFragment : Fragment(), TitleProvider {

    private var _binding: FragmentProfileDescHorizontalBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentProfileDescHorizontalBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileDescHorizontalBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvProfileName.text = "${MainActivity.currentUserSecondName} ${MainActivity.currentUserFirstName}"
        binding.tvEmail.text = "${MainActivity.currentUserEmail}"

        if (MainActivity.currentUserPFP != null) {
            Glide.with(binding.ivPFP64)
                .load(MainActivity.currentUserPFP) // Загрузка изображения по ссылке
                .into(binding.ivPFP64) // Установка изображения в ImageView
        }

        binding.btnEdit.setOnClickListener{
            with(activity as? MainActivity) {
                this?.setFragment(R.id.fragmentLayout, ProfileEditFragment.newInstance(), null, true, true)
            }

//            parentFragmentManager
//                .beginTransaction().apply {
//                    replace(R.id.fragmentLayout, ProfileEditFragment.newInstance())
//                    commit()
//                }
        }
    }

    override fun getTitle(): String {
        return "Редактировать профиль"
    }

    companion object {
        fun newInstance() = ProfileDescHorizontalFragment()
    }
}