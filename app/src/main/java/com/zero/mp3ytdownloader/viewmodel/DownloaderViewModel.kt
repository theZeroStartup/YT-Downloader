package com.zero.mp3ytdownloader.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zero.mp3ytdownloader.base.viewmodel.BaseViewModel
import com.zero.mp3ytdownloader.util.CommonUtils.formatFileSize
import com.zero.mp3ytdownloader.view.fragment.VideoDownloaderFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class DownloaderViewModel @Inject constructor(
    app: Application) : BaseViewModel(app){

    private var downloadFileJob: Job? = null
    private var isDownloadFailed = false

    val onDownloadPercentageChange = MutableLiveData<Pair<Int, String>>()
    val onDownloadFailed = MutableLiveData<String>()
    val onDownloadCompleted = MutableLiveData<File>()

    fun download(url: String, file: File) {
        if (file.exists()) {
            downloadFileJob = viewModelScope.launch(Dispatchers.IO) {
                Log.d("TAG", "download: $url")
                val httpClient = OkHttpClient()
                val request = Request.Builder().url(url).get().build()
                val call = httpClient.newCall(request)
                try {
                    val response = call.execute()
                    if (response.code == 200) {
                        var inputStream: BufferedInputStream? = null
                        try {
                            val fos = BufferedOutputStream(FileOutputStream(file))
                            if (response.body != null) {
                                inputStream = BufferedInputStream(response.body?.byteStream())
                                val totalSize =
                                    response.body?.contentLength()?.toFloat() //size of file

                                val buffer = ByteArray(32 * 1024)
                                var length = -1
                                var percent = 0F
                                var downloaded = 0f
                                while (isActive && inputStream.read(buffer).also { length = it } != -1) {
                                    fos.write(buffer, 0, length)
                                    downloaded += length
                                    totalSize?.let { percent = (downloaded * 100 / it) }
                                    val strDownloaded = formatFileSize(downloaded.toLong())
                                    val strFileSize = totalSize?.toLong()?.let { formatFileSize(it) }

                                    Log.d("TAG", "download: $percent% ${strDownloaded.plus("/").plus(strFileSize)}")
                                    onDownloadPercentageChange.postValue(Pair(percent.toInt(),
                                        strDownloaded.plus("/").plus(strFileSize)))
                                }
                                fos.close()
                            }
                        } catch (e: CancellationException) {
                            isDownloadFailed = true
                            onDownloadFailed.postValue("Download was canceled: ${e.localizedMessage}")
                        } catch (ignore: IOException) {
                            isDownloadFailed = true
                            onDownloadFailed.postValue(ignore.localizedMessage)
                        } finally {
                            inputStream?.close()
                            if (!isDownloadFailed)
                                onDownloadCompleted.postValue(file)
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

    fun cancelDownload() {
        if (downloadFileJob != null) downloadFileJob?.cancel()
    }

    fun navigateToConverterFragment(title: String, path: String) {
        navigate(VideoDownloaderFragmentDirections.actionVideoDownloaderFragmentToMoveToLocationFragment(title, path))
    }

    fun navigateToSuccessFragment(uri: String) {
        navigate(VideoDownloaderFragmentDirections.actionVideoDownloaderFragmentToDownloadSuccessFragment(uri))
    }

    fun navigateToFailureFragment(error: String) {
        navigate(VideoDownloaderFragmentDirections.actionVideoDownloaderFragmentToDownloadFailureFragment(error))
    }

}