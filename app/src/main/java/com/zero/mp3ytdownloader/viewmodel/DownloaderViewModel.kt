package com.zero.mp3ytdownloader.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zero.mp3ytdownloader.base.viewmodel.BaseViewModel
import com.zero.mp3ytdownloader.view.fragment.VideoDownloaderFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class DownloaderViewModel @Inject constructor(
    app: Application) : BaseViewModel(app){

    private var isDownloadFailed = false

    val onDownloadPercentageChange = MutableLiveData<Pair<Int, String>>()
    val onDownloadFailed = MutableLiveData<String>()
    val onDownloadCompleted = MutableLiveData<String>()

    fun download(url: String, file: File) {
        if (file.exists()) {
            viewModelScope.launch(Dispatchers.IO) {
                val httpClient = OkHttpClient()
                val call = httpClient.newCall(Request.Builder().url(url).get().build())
                try {
                    val response = call.execute()
                    if (response.code == 200) {
                        var inputStream: InputStream? = null
                        try {
                            val fos = FileOutputStream(file)
                            if (response.body != null) {
                                inputStream = response.body!!.byteStream()
                                val totalSize =
                                    response.body?.contentLength()?.toFloat() //size of file

                                val buffer = ByteArray(1024)
                                var length: Int
                                var percent = 0F
                                var downloaded = 0f
                                while (inputStream.read(buffer).also { length = it } != -1) {
                                    fos.write(buffer, 0, length)
                                    downloaded += length
                                    totalSize?.let { percent = (downloaded * 100 / it) }
                                    val strDownloaded = formatFileSize(downloaded.toLong())
                                    val strFileSize = totalSize?.toLong()?.let { formatFileSize(it) }

                                    onDownloadPercentageChange.postValue(Pair(percent.toInt(),
                                        strDownloaded.plus("/").plus(strFileSize)))
                                }
                                fos.close()
                            }
                        } catch (ignore: IOException) {
                            isDownloadFailed = true
                            onDownloadFailed.postValue(ignore.localizedMessage)
                        } finally {
                            inputStream?.close()
                            if (!isDownloadFailed)
                                onDownloadCompleted.postValue(file.path)
                        }
                    } else {
                        onDownloadFailed.postValue(response.message)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    onDownloadFailed.postValue(e.localizedMessage)
                }
            }
        }
    }

    private fun formatFileSize(size: Long): String {
        var hrSize: String? = null
        val b = size.toDouble()
        val k = size / 1024.0
        val m = size / 1024.0 / 1024.0
        val g = size / 1024.0 / 1024.0 / 1024.0
        val t = size / 1024.0 / 1024.0 / 1024.0 / 1024.0
        val dec = DecimalFormat("0.00")
        hrSize = if (t > 1) {
            dec.format(t).plus(" TB")
        } else if (g > 1) {
            dec.format(g).plus(" GB")
        } else if (m > 1) {
            dec.format(m).plus(" MB")
        } else if (k > 1) {
            dec.format(k).plus(" KB")
        } else {
            dec.format(b).plus(" Bytes")
        }
        return hrSize
    }

    fun navigateToConverterFragment(title: String, path: String) {
        navigate(VideoDownloaderFragmentDirections.actionVideoDownloaderFragmentToMoveToLocationFragment(title, path))
    }

    fun navigateToFailureFragment(error: String) {
        navigate(VideoDownloaderFragmentDirections.actionVideoDownloaderFragmentToDownloadFailureFragment(error))
    }

}