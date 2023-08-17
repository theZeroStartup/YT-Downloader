package com.zero.mp3ytdownloader.viewmodel

import android.app.Application
import android.net.Uri
import com.zero.mp3ytdownloader.base.viewmodel.BaseViewModel
import com.zero.mp3ytdownloader.datasource.local.SharedPrefDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    app: Application,
    private val dataSource: SharedPrefDelegate
) : BaseViewModel(app){

    fun getStoragePath(): Uri? {
        return dataSource.getStoragePath()
    }
}