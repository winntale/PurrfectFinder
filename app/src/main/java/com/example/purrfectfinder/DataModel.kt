package com.example.purrfectfinder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class DataModel : ViewModel() {
    val filteredAds: MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }
}