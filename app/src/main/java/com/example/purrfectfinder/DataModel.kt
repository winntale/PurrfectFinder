package com.example.purrfectfinder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.purrfectfinder.SerializableDataClasses.Advertisement

open class DataModel : ViewModel() {
    val filteredAds: MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }

    val allAds: MutableLiveData<List<Advertisement>> by lazy {
        MutableLiveData<List<Advertisement>>()
    }

    val favAdsIds: MutableLiveData<List<Int>> by lazy {
        MutableLiveData<List<Int>>()
    }

    val favAds: MutableLiveData<List<Advertisement>> by lazy {
        MutableLiveData<List<Advertisement>>()
    }
}