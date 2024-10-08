package com.example.purrfectfinder.Registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.purrfectfinder.Login.LoginActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.ActivityRegistrationBinding
import com.example.purrfectfinder.databinding.ActivityRegistrationFinishBinding

class RegistrationFinishActivity : AppCompatActivity() {
    private var _binding : ActivityRegistrationFinishBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityRegistrationFinishBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationFinishBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnDone.setOnClickListener {
            val intent = Intent(this@RegistrationFinishActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}