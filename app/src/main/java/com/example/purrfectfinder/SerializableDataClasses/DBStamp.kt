package com.example.purrfectfinder.SerializableDataClasses

import com.example.purrfectfinder.SerializableDataClasses.Filters.Breeds
import com.example.purrfectfinder.SerializableDataClasses.Filters.Colors

@kotlinx.serialization.Serializable
data class DBStamp(
    val breeds: List<Breeds>,
    val colors: List<Colors>,
    val ads: List<Advertisement>,
    val favAds: List<Int>
)