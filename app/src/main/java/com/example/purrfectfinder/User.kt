package com.example.purrfectfinder

@kotlinx.serialization.Serializable
data class User(val email: String = "",
                val password: String = "",
                val secondName: String = "",
                val firstName: String = "",
                val middleName: String? = "",
                val birthday: String = "",
                val role: String = "",
                val gender: String = "")