package com.example.purrfectfinder.Registration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.purrfectfinder.DbHelper
import com.example.purrfectfinder.R
import com.example.purrfectfinder.User
import com.example.purrfectfinder.databinding.ActivityRegistration2Binding

class Registration2Activity : AppCompatActivity() {
    private var _binding : ActivityRegistration2Binding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("Binding for ActivityRegistration2Binding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistration2Binding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bundleReceived = intent.getBundleExtra("BUNDLE")

        binding.btnPrev.setOnClickListener {
            val intentToFirstPage = Intent(this@Registration2Activity, RegistrationActivity::class.java)
            startActivity(intentToFirstPage)
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.roles_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.roleSpinner.adapter = adapter
        }

        binding.radioMan.setOnCheckedChangeListener { btn, isChecked ->
            Log.d("RADIO", "man is checked: $isChecked")
        }

        binding.radioWoman.setOnCheckedChangeListener { btn, isChecked ->
            Log.d("RADIO", "woman is checked: $isChecked")
        }

        binding.btnNext.setOnClickListener {
            val emailReceived = bundleReceived?.getString("EMAIL") ?: ""
            val passwordReceived = bundleReceived?.getString("PASSWORD") ?: ""


            val secondName = binding.etSecondName.text.toString().trim()
            val firstName = binding.etFirstName.text.toString().trim()
            val middleName = binding.etMiddleName.text.toString().trim()
            val birthday = binding.etBirthday.text.toString().trim()
            val role = binding.roleSpinner.selectedItem.toString().trim()
            val gender =
                if (binding.radioMan.isChecked) "Мужской"
                else "Женский"

            if (isFieldsValid(secondName, firstName, middleName, birthday)) {
                val user = User(emailReceived, passwordReceived, secondName, firstName, middleName, birthday, role, gender)

                val db = DbHelper(this, null)
                db.addUser(user)

                Toast.makeText(this, "Пользователь $secondName $firstName добавлен", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun isFieldsValid(secondName : String, firstName : String, middleName : String?, birthday : String) : Boolean {
        with(binding) {

            if (secondName == "" && firstName == "") {
                lSecondName.helperText = ContextCompat.getString(
                    this@Registration2Activity,
                    R.string.error_second_name
                )
                lFirstName.helperText = ContextCompat.getString(
                    this@Registration2Activity,
                    R.string.error_first_name
                )

                return false
            }

            if (secondName == "") {
                lSecondName.helperText = ContextCompat.getString(
                    this@Registration2Activity,
                    R.string.error_second_name
                )

                return false
            }
            else {
                lSecondName.helperText = ""
            }

            if (firstName == "") {
                lFirstName.helperText = ContextCompat.getString(
                    this@Registration2Activity,
                    R.string.error_first_name
                )

                return false
            }
            else {
                lFirstName.helperText = ""
            }

        }

        return true
    }
}