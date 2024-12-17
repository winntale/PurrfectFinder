package com.example.purrfectfinder.Adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.purrfectfinder.Fragments.ProfileAboutFragment
import com.example.purrfectfinder.Fragments.ProfilePhotoFragment
import java.lang.IllegalStateException

class TabsPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private var numberOfTabs: Int,
    private var userId: Int
) : FragmentStateAdapter(fm, lifecycle) {


    private var _createdFragList : MutableList<Fragment>? = null
    public val createdFragList : MutableList<Fragment>
        get() = _createdFragList
            ?: throw IllegalStateException("FragmentList must not be null")


    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                // Photo Fragment
                val bundle = Bundle()
                bundle.putInt("userId", userId)
                val photoFragment = ProfilePhotoFragment.newInstance()
                photoFragment.arguments = bundle
                _createdFragList?.add(photoFragment)
                return photoFragment
            }
            1 -> {
                // About Fragment
                val bundle = Bundle()
                bundle.putInt("userId", userId)
                val aboutFragment = ProfileAboutFragment.newInstance()
                aboutFragment.arguments = bundle
                _createdFragList?.add(aboutFragment)
                return aboutFragment
            }
            else -> return ProfilePhotoFragment.newInstance()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}