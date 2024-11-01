package com.example.purrfectfinder

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.example.purrfectfinder.Fragments.ProfileDescHorizontalFragment
import com.example.purrfectfinder.Fragments.ProfileDescriptionFragment
import com.example.purrfectfinder.Fragments.ProfileFragment
import com.example.purrfectfinder.Fragments.SettingsFragment
import com.example.purrfectfinder.databinding.ActivityMainBinding
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
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

        getData(db)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        supportFragmentManager
            .beginTransaction().apply {
                replace(R.id.profileLayout, ProfileDescriptionFragment.newInstance())
                replace(R.id.fragmentLayout, ProfileFragment.newInstance())
                commit()
            }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

            supportFragmentManager
                .beginTransaction().apply {
                    replace(R.id.profileLayout, ProfileDescriptionFragment.newInstance())
                    replace(R.id.fragmentLayout, ProfileFragment.newInstance())
                    commit()
                }
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

            supportFragmentManager
                .beginTransaction().apply {
                    replace(R.id.profileLayout, ProfileDescHorizontalFragment.newInstance())
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

    private fun getData(db : DbHelper) {
        lifecycleScope.launch {
            val client = db.getClient()
            val supabaseResponse = client.postgrest["Users"].select()
            val data = supabaseResponse.decodeList<User>()
            Log.e("supabase", data.toString())
        }
    }
}