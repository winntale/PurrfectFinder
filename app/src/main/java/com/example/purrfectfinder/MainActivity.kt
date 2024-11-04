package com.example.purrfectfinder

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.purrfectfinder.Fragments.AdvertisementsFragment
import com.example.purrfectfinder.Fragments.ProfileDescHorizontalFragment
import com.example.purrfectfinder.Fragments.ProfileDescriptionFragment
import com.example.purrfectfinder.Fragments.ProfileFragment
import com.example.purrfectfinder.Fragments.SettingsFragment
import com.example.purrfectfinder.Registration.Registration2Activity.Companion.EMAIL
import com.example.purrfectfinder.SerializableDataClasses.User
import com.example.purrfectfinder.databinding.ActivityMainBinding
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = DbHelper()

        lifecycleScope.launch {
            db.getData<User>("Users")
            return@launch
        }

        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundleReceived = intent.getBundleExtra("BUNDLE")
        currentUserId = bundleReceived?.getInt(ID)!!

        lifecycleScope.launch {
            allFavs = db.getAllFavAds(currentUserId!!)
            return@launch
        }

        binding.btnPrev.setOnClickListener {
            binding.btnPrev.visibility = GONE
            binding.btnSettings.visibility = VISIBLE

            binding.tvWinTitle.text = ContextCompat.getString(
                this@MainActivity,
                R.string.your_profile
            )
            binding.tvWinTitle.textSize = 23f
            binding.tvWinTitle.setPadding(0, 0, 0, 0)

            setFragment(R.id.profileLayout, ProfileDescriptionFragment.newInstance())
            setFragment(R.id.fragmentLayout, ProfileFragment.newInstance())
        }

        binding.btnSettings.setOnClickListener {
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE

            binding.tvWinTitle.text = ContextCompat.getString(
                this@MainActivity,
                R.string.settings
            )
            binding.tvWinTitle.textSize = 23f
            binding.tvWinTitle.setPadding(0, 0, 0, 0)

            setFragment(R.id.profileLayout, ProfileDescHorizontalFragment.newInstance())
            setFragment(R.id.fragmentLayout, SettingsFragment.newInstance())
        }

        // navigationview

        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = 0)
            insets
        }

        binding.navigationView.selectedItemId = R.id.profile

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, null)
                }
                R.id.favourites -> {
                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, null)
                }
                R.id.petshop -> {
                    binding.profileLayout.visibility = GONE
                    setFragment(R.id.fragmentLayout, AdvertisementsFragment.newInstance())
                }
                R.id.clips -> {
                    setFragment(R.id.profileLayout, null)
                    setFragment(R.id.fragmentLayout, null)
                }
                R.id.profile -> {
                    binding.profileLayout.visibility = VISIBLE
                    setFragment(R.id.profileLayout, ProfileDescriptionFragment.newInstance())
                    setFragment(R.id.fragmentLayout, ProfileFragment.newInstance())
                }
            }
            true
        }

//        supportActionBar?.hide()
    }


    fun updateHeaderOnEdit() {
        binding.btnPrev.visibility = VISIBLE
        binding.btnSettings.visibility = GONE

        binding.tvWinTitle.text = ContextCompat.getString(
            this@MainActivity,
            R.string.edit_profile
        )
        binding.tvWinTitle.textSize = 17f
        binding.tvWinTitle.setPadding(0, 13, 0, 0)
    }

    fun setFragment(layout: Int, fragment: Fragment?) {
        supportFragmentManager
            .beginTransaction().apply {
                if (fragment != null) {
                    replace(layout, fragment)
                } else {
                    // Если передан null, удаляем все фрагменты из контейнера
                    supportFragmentManager.findFragmentById(layout)?.let {
                        remove(it)
                    }
                }
                commit()
            }
    }

    companion object {
        const val ID = "id"
        var currentUserId: Int? = null
        var allFavs: List<Int>? = null
    }
}