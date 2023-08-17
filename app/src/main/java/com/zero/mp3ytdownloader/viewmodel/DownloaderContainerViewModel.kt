package com.zero.mp3ytdownloader.viewmodel

import android.app.Application
import com.zero.mp3ytdownloader.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DownloaderContainerViewModel @Inject constructor(
    app: Application) : BaseViewModel(app)