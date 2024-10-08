package com.example.purrfectfinder.Registration

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.ActivityRegistrationBinding
import com.example.purrfectfinder.databinding.ActivityRegistrationPostBinding

class RegistrationPostActivity : AppCompatActivity() {
    private var _binding : ActivityRegistrationPostBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityRegistrationPostBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationPostBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnPrev.setOnClickListener {
            val intent = Intent(this@RegistrationPostActivity, RegistrationTestActivity::class.java)
            startActivity(intent)
        }

        binding.btnSkip.setOnClickListener {
            val intent = Intent(this@RegistrationPostActivity, RegistrationFinishActivity::class.java)
            startActivity(intent)
        }
    }
}