package com.zero.mp3ytdownloader.datasource.local

import android.net.Uri


class SharedPrefDelegateImpl(private val authSharedPrefs: SharedPreferenceUtils):
    SharedPrefDelegate {

    companion object {
        private const val SELECTED_DIRECTORY = "selected_dir"
    }

    override fun saveStoragePath(path: String) {
        authSharedPrefs.saveString(SELECTED_DIRECTORY, path)
    }

    override fun getStoragePath(): Uri? {
        return Uri.parse(authSharedPrefs.getString(SELECTED_DIRECTORY, ""))
    }

}