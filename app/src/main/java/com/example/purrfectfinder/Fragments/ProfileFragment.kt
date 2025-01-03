package com.example.purrfectfinder.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.purrfectfinder.Adapters.TabsPagerAdapter
import com.example.purrfectfinder.DataModel
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.interfaces.TitleProvider
import com.example.purrfectfinder.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(), TitleProvider {
    private val dataModel: DataModel by activityViewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for FragmentProfileBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments

        // 0: userId
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

        dataModel.isPostsLoaded.observe(viewLifecycleOwner) {
            val context = requireContext()
            val displayMetrics = context.resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels / displayMetrics.density

            val paddingBottom = (screenHeight * 0.35 * displayMetrics.density).toInt()

            binding.tabsViewpager.setPadding(0, 0, 0, paddingBottom)
            binding.tabsViewpager.requestLayout()
        }
    }

    override fun getTitle(): String {
        return "Ваш профиль"
    }

    companion object {
        fun newInstance() = ProfileFragment()
        var args: List<String> = emptyList()
    }
}