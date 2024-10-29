package com.example.purrfectfinder.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.ActivityMainBinding
import com.example.purrfectfinder.databinding.FragmentProfileBinding
import com.example.purrfectfinder.databinding.FragmentProfileDescriptionBinding

class ProfileDescriptionFragment : Fragment() {

    private var _binding: FragmentProfileDescriptionBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentProfileDescriptionBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEdit.setOnClickListener{
            (activity as? MainActivity)?.updateHeaderOnEdit()

            parentFragmentManager
                .beginTransaction().apply {
                    replace(R.id.profileLayout, ProfileDescHorizontalFragment.newInstance())
                    replace(R.id.fragmentLayout, ProfileEditFragment.newInstance())
                    commit()
                }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileDescriptionFragment()
    }
}