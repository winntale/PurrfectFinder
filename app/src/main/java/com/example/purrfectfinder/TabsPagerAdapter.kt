package com.example.purrfectfinder

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.purrfectfinder.Fragments.ProfileAboutFragment
import com.example.purrfectfinder.Fragments.ProfilePhotoFragment

class TabsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private var numberOfTabs: Int) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                // # Photo Fragment
                val bundle = Bundle()
                bundle.putString("fragmentName", "Photo Fragment")
                val photoFragment = ProfilePhotoFragment.newInstance()
                photoFragment.arguments = bundle
                return photoFragment
            }
            1 -> {
                // # About Fragment
                val bundle = Bundle()
                bundle.putString("fragmentName", "About Fragment")
                val aboutFragment = ProfileAboutFragment.newInstance()
                aboutFragment.arguments = bundle
                return aboutFragment
            }
            else -> return ProfilePhotoFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}