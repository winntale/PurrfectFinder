package com.example.purrfectfinder

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.purrfectfinder.Login.LoginActivity
import com.example.purrfectfinder.Registration.RegistrationActivity
import com.example.purrfectfinder.databinding.ActivityAuthorizationBinding

class AuthorizationActivity : AppCompatActivity() {
    private var _binding : ActivityAuthorizationBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityAuthorizationBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthorizationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@AuthorizationActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegistration.setOnClickListener {
            val intent = Intent(this@AuthorizationActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }


}