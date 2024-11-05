package com.example.purrfectfinder.Registration

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.ActivityRegistrationTestBinding

class RegistrationTestActivity : AppCompatActivity() {
    private var _binding : ActivityRegistrationTestBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityRegistrationTestBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationTestBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnPrev.setOnClickListener {
            val intent = Intent(this@RegistrationTestActivity, Registration2Activity::class.java)
            startActivity(intent)
        }

        binding.btnSkip.setOnClickListener {
            val intent = Intent(this@RegistrationTestActivity, RegistrationPostActivity::class.java)
            startActivity(intent)
        }
    }
}