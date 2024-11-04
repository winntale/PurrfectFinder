package com.example.purrfectfinder.SerializableDataClasses

@kotlinx.serialization.Serializable
data class Advertisement(val id: Int?,
                         val name: String = "",
                         val price: Float? = null,
                         val picture: String = "") {
}