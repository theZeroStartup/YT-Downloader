package com.zero.mp3ytdownloader.base.primarynav

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.zero.mp3ytdownloader.base.viewmodel.BaseViewModel

class PrimaryNavViewModel(app: Application) : BaseViewModel(app) {

    val primaryNavModelLiveData: LiveData<PrimaryNavModel>
        get() = _primaryNavModelLiveData

    private val _primaryNavModelLiveData = MutableLiveData<PrimaryNavModel>()

    private lateinit var cachedModelLiveData: LiveData<PrimaryNavModel>
    private val observer = Observer<PrimaryNavModel?> {
        _primaryNavModelLiveData.value = it
    }

    override fun onCleared() {
        if (!::cachedModelLiveData.isInitialized){
            cachedModelLiveData.removeObserver(observer)
        }
        super.onCleared()
    }
}