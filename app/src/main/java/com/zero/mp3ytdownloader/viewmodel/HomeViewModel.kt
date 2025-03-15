package com.zero.mp3ytdownloader.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.google.gson.Gson
import com.zero.mp3ytdownloader.base.viewmodel.BaseViewModel
import com.zero.mp3ytdownloader.datasource.local.SharedPrefDelegate
import com.zero.mp3ytdownloader.model.MediaDetails
import com.zero.mp3ytdownloader.util.Constants.AUDIO_CHANNELS
import com.zero.mp3ytdownloader.util.Constants.AUDIO_CODEC
import com.zero.mp3ytdownloader.util.Constants.BITRATE
import com.zero.mp3ytdownloader.util.Constants.GET_AUDIO_DETAILS
import com.zero.mp3ytdownloader.util.Constants.FORMATS
import com.zero.mp3ytdownloader.util.Constants.GET_VIDEO_DETAILS
import com.zero.mp3ytdownloader.util.Constants.LIKE_COUNT
import com.zero.mp3ytdownloader.util.Constants.THUMBNAIL
import com.zero.mp3ytdownloader.util.Constants.TITLE
import com.zero.mp3ytdownloader.util.Constants.URL
import com.zero.mp3ytdownloader.util.Constants.VIDEO_CODEC
import com.zero.mp3ytdownloader.util.Constants.VIEW_COUNT
import com.zero.mp3ytdownloader.view.fragment.HomeFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    app: Application,
    private val py: Python,
    private val dataSource: SharedPrefDelegate
) : BaseViewModel(app){

    val videoDetails = MutableLiveData<MediaDetails>()
    val extractionError = MutableLiveData<Boolean>()

    fun grabVideoDetails(link: String, isOnlyAudio: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val youtubeExtractor = py.getModule("yt-dl")
            val details = youtubeExtractor.callAttr(GET_VIDEO_DETAILS, link)?.asMap()

            if (details != null) {
                val formats = PyObject.fromJava(FORMATS)
                val audioChannels = PyObject.fromJava(AUDIO_CHANNELS)
                val thumbnail = PyObject.fromJava(THUMBNAIL)
                val title = PyObject.fromJava(TITLE)
                val viewCount = PyObject.fromJava(VIEW_COUNT)
                val likeCount = PyObject.fromJava(LIKE_COUNT)
                val videoCodec = PyObject.fromJava(VIDEO_CODEC)
                val audioCodec = PyObject.fromJava(AUDIO_CODEC)
                val url = PyObject.fromJava(URL)
                val abr = PyObject.fromJava(BITRATE)

                val strThumbnail = details[thumbnail].toString()
                val strTitle = details[title].toString()
                val strViewCount = details[viewCount].toString()
                val strLikeCount = details[likeCount].toString()

                var downloadUrl = ""
                var max = 0F

                val videoFormatDetails = details[formats]?.asMap()
                if (videoFormatDetails != null) {
                    for (video in videoFormatDetails) {
                        val aCodec = video.key.asMap()[audioCodec].toString()
                        val vCodec = video.key.asMap()[videoCodec].toString()

                        if (aCodec.isNotEmpty() && !aCodec.contains("none") &&
                            vCodec.isNotEmpty() && !vCodec.contains("none")) {
                            downloadUrl = video.key.asMap()[url].toString()
                        }
                    }
                }

                if (isOnlyAudio && downloadUrl.isEmpty()) {
                    if (videoFormatDetails != null) {
                        for (video in videoFormatDetails) {
                            val audioChannel = video.key.asMap()[audioChannels].toString().toIntOrNull()
                            if (audioChannel != null){
                                val bitrate = video.key.asMap()[abr].toString().toFloatOrNull()

                                if (bitrate != null && bitrate > max){
                                    max = bitrate
                                    downloadUrl = video.key.asMap()[url].toString()
                                }
                            }
                        }
                    }
                }

                Log.d("TAG", "grabVideoDetails: $downloadUrl")
                if (downloadUrl.isNotEmpty()) {
                    val mediaDetails = MediaDetails(
                        strLikeCount,
                        strViewCount,
                        strThumbnail,
                        downloadUrl,
                        strTitle,
                        isOnlyAudio
                    )
                    videoDetails.postValue(mediaDetails)
                }
                else extractionError.postValue(true)
            }
            else extractionError.postValue(true)
        }
    }


    fun saveStoragePath(path: String) {
        dataSource.saveStoragePath(path)
    }

    fun getStoragePath(): Uri? {
        return dataSource.getStoragePath()
    }

    fun navigateToDownloadFragment(mediaDetails: MediaDetails) {
        navigate(HomeFragmentDirections.actionHomeFragmentToVideoDownloaderFragment(Gson().toJson(mediaDetails)))
    }
}