
package com.example.purrfectfinder.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.FragmentAdvertisementsBinding
import com.example.purrfectfinder.databinding.FragmentLoadingBinding

class LoadingFragment : Fragment() {

    private lateinit var loadingTextView: TextView

    private var _binding: FragmentLoadingBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentLoadingBinding must not be null")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingTextView = binding.loadingTextView
    }

    fun updateLoadingText(text: String) {
        if (::loadingTextView.isInitialized) {
            loadingTextView.text = text
        }
    }

    companion object {
        fun newInstance() = LoadingFragment()
    }
}
