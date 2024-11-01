package com.example.purrfectfinder.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.purrfectfinder.R
import com.example.purrfectfinder.TabsPagerAdapter
import com.example.purrfectfinder.databinding.ActivityMainBinding
import com.example.purrfectfinder.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {
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
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberOfTabs = 2
        binding.tabLayout.tabMode = TabLayout.MODE_FIXED
        binding.tabLayout.isInlineLabel = true

        val adapter = TabsPagerAdapter(parentFragmentManager, lifecycle, numberOfTabs)
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

                val view = // get the view
                    view.post {
                        val wMeasureSpec = MeasureSpec.makeMeasureSpec(view.width, MeasureSpec.EXACTLY)
                        val hMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                        view.measure(wMeasureSpec, hMeasureSpec)

                        if (binding.tabsViewpager.layoutParams.height != view.measuredHeight) {
                            // ParentViewGroup is, for example, LinearLayout
                            // ... or whatever the parent of the ViewPager2 is
                            binding.tabsViewpager.layoutParams = (binding.tabsViewpager.layoutParams as ViewGroup.LayoutParams)
                                .also { lp -> lp.height = view.measuredHeight }
                        }
                    }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}