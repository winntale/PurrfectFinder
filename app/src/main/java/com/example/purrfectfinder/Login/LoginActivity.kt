package com.example.purrfectfinder.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.MainActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.Registration.RegistrationActivity
import com.example.purrfectfinder.databinding.ActivityLoginBinding
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private var _binding : ActivityLoginBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityLoginBinding must not be null")

    private var userBundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnReg.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val inputedEmail = binding.etEmail.text.toString().trim()
            val inputedPassword = binding.etPassword.text.toString().trim()

            if (isFieldsValid(inputedEmail, inputedPassword)) {
                val db = DbHelper()

                lifecycleScope.launch {
                    val client = db.getClient()

                    client.auth.signInWith(Email) {
                        email = inputedEmail
                        password = inputedPassword
                    }

                    val session = client.auth.currentSessionOrNull()
                    Log.e("session", session.toString())
                    if (session != null) {
                        val user = db.getUser(inputedEmail, inputedPassword)!!
                        userBundle.putInt(MainActivity.ID, user.id!!)
                        userBundle.putString(MainActivity.EMAIL, user.email)
                        userBundle.putString(MainActivity.PASSWORD, user.password)
                        userBundle.putString(MainActivity.SECONDNAME, user.secondName)
                        userBundle.putString(MainActivity.FIRSTNAME, user.firstName)
                        userBundle.putString(MainActivity.CREATEDAT, user.createdAt)
                        userBundle.putString(MainActivity.PFP, user.pfp)
                        successfulAuth()
                    }
                    else {
                        unsuccessfulAuth()
                    }
                }


            }
            else {
                unsuccessfulAuth()
            }
        }
    }

    private fun isFieldsValid(email: String, password: String) : Boolean {
        with(binding) {

            if (email == "" && password == "") {
                lEmail.helperText = ContextCompat.getString(
                    this@LoginActivity,
                    R.string.error_email
                )
                lPassword.helperText = ContextCompat.getString(
                    this@LoginActivity,
                    R.string.error_password
                )

                return false
            }

            if (email == "") {
                lEmail.helperText = ContextCompat.getString(
                    this@LoginActivity,
                    R.string.error_email
                )

                return false
            }
            else {
                lEmail.helperText = ""
            }

            if (password == "") {
                lPassword.helperText = ContextCompat.getString(
                    this@LoginActivity,
                    R.string.error_password
                )

                return false
            }
            else {
                lPassword.helperText = ""
            }
        }

        return true
    }

    private fun successfulAuth() {
        Toast.makeText(this, "Успешная авторизация", Toast.LENGTH_LONG).show()
        binding.etEmail.text?.clear()
        binding.etPassword.text?.clear()

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("BUNDLE", userBundle)
        startActivity(intent)
    }

    private fun unsuccessfulAuth() {
        Toast.makeText(this, "Что-то пошло не так, проверьте введенные данные", Toast.LENGTH_LONG).show()
    }
}