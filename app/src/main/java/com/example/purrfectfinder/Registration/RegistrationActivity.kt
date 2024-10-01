package com.example.purrfectfinder.Registration

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.ActivityOnBoardingBinding
import com.example.purrfectfinder.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private var _binding : ActivityRegistrationBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityAuthorizationBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }


}