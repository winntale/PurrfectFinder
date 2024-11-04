package com.example.purrfectfinder.SerializableDataClasses

@kotlinx.serialization.Serializable
data class User(val id: Int? = null,
                val email: String = "",
                val password: String = "",
                val secondName: String = "",
                val firstName: String = "",
                val middleName: String? = "",
                val birthday: String = "",
                val role: String = "",
                val gender: String = "",
                val createdAt: String = "",
                val pfp: String? = null)