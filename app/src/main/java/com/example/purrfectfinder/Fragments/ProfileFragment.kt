package com.example.purrfectfinder.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.purrfectfinder.Adapters.TabsPagerAdapter
import com.example.purrfectfinder.interfaces.TitleProvider
import com.example.purrfectfinder.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment(), TitleProvider {
    private val fragList = listOf(
        ProfilePhotoFragment.newInstance(),
        ProfileAboutFragment.newInstance()
    )

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentProfileBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments
        args = bundle!!.getStringArrayList("args")!!.toList()

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberOfTabs = 2
        binding.tabLayout.tabMode = TabLayout.MODE_FIXED
        binding.tabLayout.isInlineLabel = true

        val adapter = TabsPagerAdapter(parentFragmentManager, lifecycle, numberOfTabs, args[0].toInt())
        binding.tabsViewpager.adapter = adapter
        binding.tabsViewpager.isUserInputEnabled = true

        TabLayoutMediator(binding.tabLayout, binding.tabsViewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Фото/Видео"
                }
                1 -> {
                    tab.text = "О себе"
                }
            }
        }.attach()

        binding.tabsViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })
    }

    override fun getTitle(): String {
        return "Ваш профиль"
    }

    companion object {
        fun newInstance() = ProfileFragment()
        var args: List<String> = emptyList()
    }
}