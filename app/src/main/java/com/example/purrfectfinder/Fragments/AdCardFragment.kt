package com.example.purrfectfinder.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        _binding =  FragmentAdCardBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun getTitle(): String {
        return ""
    }

    companion object {
        fun newInstance() = AdCardFragment()
    }
}