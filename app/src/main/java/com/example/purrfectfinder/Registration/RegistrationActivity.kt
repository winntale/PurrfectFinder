package com.example.purrfectfinder.Registration

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.purrfectfinder.AuthorizationActivity
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.Login.LoginActivity
import com.example.purrfectfinder.R
import com.example.purrfectfinder.databinding.ActivityRegistrationBinding
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {
    private var _binding: ActivityRegistrationBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityRegistrationBinding must not be null")

    private var userBundle = Bundle()

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


        binding.btnPrev.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, AuthorizationActivity::class.java)
            startActivity(intent)
        }

        binding.btnNext.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val passwordConfirm = binding.etRepeatPassword.text.toString().trim()

            lifecycleScope.launch {
                val user = DbHelper.getInstance().getUser(email, password)

                if (isFieldsValid(email, password, passwordConfirm)) {
                    if (user != null) {
                        binding.lEmail.helperText = ContextCompat.getString(
                            this@RegistrationActivity,
                            R.string.user_exist
                        )
                    }
                    else {
                        userBundle.putString(Registration2Activity.EMAIL, email)
                        userBundle.putString("PASSWORD", password)

                        val intent = Intent(this@RegistrationActivity, Registration2Activity::class.java)
                        intent.putExtra("BUNDLE", userBundle)
                        startActivity(intent)
                    }
                }


            }




        }

        binding.btnAccountExists.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isFieldsValid(email: String, password: String, passwordConfirm: String): Boolean {
        with(binding) {

            if (email == "" && password == "" && passwordConfirm == "") {
                lEmail.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_email
                )
                lPassword.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_password
                )
                lRepeatPassword.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_password
                )

                return false
            }

            if (password == "" && passwordConfirm == "") {
                lPassword.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_password
                )
                lRepeatPassword.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_password
                )

                return false
            }

            if (email == "") {
                lEmail.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_email
                )
            }
            else {
                lEmail.helperText = ""
            }

            if (password == "") {
                lPassword.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_password
                )

                return false
            }
            else {
                lPassword.helperText = ""
            }

            if (passwordConfirm == "") {
                lRepeatPassword.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_password
                )

                return false
            }
            else {
                lRepeatPassword.helperText = ""
            }

            if (password != passwordConfirm) {
                lPassword.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.different_passwords
                )
                lRepeatPassword.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.different_passwords
                )

                return false
            }
            else {
                lPassword.helperText = ""
                lRepeatPassword.helperText = ""
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                lEmail.helperText = ContextCompat.getString(
                    this@RegistrationActivity,
                    R.string.error_email
                )

                return false
            }

            if (!cbConfPolicy.isChecked) {
                return false
            }
        }

        return true
    }


}