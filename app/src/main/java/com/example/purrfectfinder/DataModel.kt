package com.example.purrfectfinder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.net.Uri
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

    val isAdsLoaded: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val imageUris: MutableLiveData<List<Uri>> by lazy {
        MutableLiveData<List<Uri>>(null)
    }

    val postPhotoUris: MutableLiveData<List<Uri>> by lazy {
        MutableLiveData<List<Uri>>(null)
    }
}