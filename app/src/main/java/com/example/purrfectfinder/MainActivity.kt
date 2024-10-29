package com.example.purrfectfinder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.purrfectfinder.Fragments.ProfileEditFragment
import com.example.purrfectfinder.Fragments.ProfileFragment
import com.example.purrfectfinder.databinding.ActivityMainBinding
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.View.GONE
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import com.example.purrfectfinder.Fragments.SettingsFragment

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        supportFragmentManager
            .beginTransaction().apply {
                replace(R.id.fragmentLayout, ProfileFragment.newInstance())
                commit()
            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnEdit.setOnClickListener{
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE

            binding.tvWinTitle.text = ContextCompat.getString(
                this@MainActivity,
                R.string.edit_profile
            )
            binding.tvWinTitle.textSize = 17f
            binding.tvWinTitle.setPadding(0, 13,0,0)

            supportFragmentManager
                .beginTransaction().apply {
                    replace(R.id.fragmentLayout, ProfileEditFragment.newInstance())
                    commit()
                }
        }

        binding.btnPrev.setOnClickListener{
            binding.btnPrev.visibility = GONE
            binding.btnSettings.visibility = VISIBLE

            binding.tvWinTitle.text = ContextCompat.getString(
                this@MainActivity,
                R.string.your_profile
            )
            binding.tvWinTitle.textSize = 23f
            binding.tvWinTitle.setPadding(0, 0,0,0)

            supportFragmentManager
                .beginTransaction().apply {
                    replace(R.id.fragmentLayout, ProfileFragment.newInstance())
                    commit()
                }
        }

        binding.btnSettings.setOnClickListener{
            binding.btnPrev.visibility = VISIBLE
            binding.btnSettings.visibility = GONE

            binding.tvWinTitle.text = ContextCompat.getString(
                this@MainActivity,
                R.string.settings
            )
            binding.tvWinTitle.textSize = 23f
            binding.tvWinTitle.setPadding(0, 0,0,0)

            supportFragmentManager
                .beginTransaction().apply {
                    replace(R.id.fragmentLayout, SettingsFragment.newInstance())
                    commit()
                }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.navigationView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = 0)
            insets
        }

//        supportActionBar?.hide()
    }
}