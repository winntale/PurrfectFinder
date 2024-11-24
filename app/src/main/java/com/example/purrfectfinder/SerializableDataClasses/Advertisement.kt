package com.example.purrfectfinder.SerializableDataClasses

@kotlinx.serialization.Serializable
data class Advertisement(val id: Int?,
                         val sellerId: Int,
                         val name: String = "",
                         val price: Float? = null,
                         val picture: String = "",
                         val verifiedBreed: Boolean = false,
                         val breedId: Int? = null,
                         val colorId: Int? = null,
                         val gender: String = "",
                         val age: Int? = null)