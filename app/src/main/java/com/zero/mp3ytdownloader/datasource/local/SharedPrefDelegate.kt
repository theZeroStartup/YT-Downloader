package com.zero.mp3ytdownloader.datasource.local

import android.net.Uri

interface SharedPrefDelegate {
    fun saveStoragePath(path: String)

    fun getStoragePath(): Uri?
}