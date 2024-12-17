package com.example.purrfectfinder.SerializableDataClasses

@kotlinx.serialization.Serializable
data class Advertisement(
    val id: Int? = null,
    val sellerId: Long,
    val name: String = "",
    val price: Double? = null,
    val picture: List<String> = emptyList(),
    val verifiedBreed: Boolean = false,
    val breedId: Long? = null,
    val colorId: Long? = null,
    val gender: String = "",
    val age: Long? = null
)