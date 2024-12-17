package com.example.purrfectfinder.SerializableDataClasses

@kotlinx.serialization.Serializable
data class Post(
    val id: Int? = null,
    val sellerId: Long,
    val name: String,
    val desc: String,
    val photo: List<String>,
    val views: Long = 0
)