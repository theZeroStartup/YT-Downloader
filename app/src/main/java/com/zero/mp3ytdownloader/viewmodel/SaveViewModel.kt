package com.zero.mp3ytdownloader.viewmodel

import android.app.Application
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.zero.mp3ytdownloader.base.viewmodel.BaseViewModel
import com.zero.mp3ytdownloader.view.fragment.MoveToLocationFragmentDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@HiltViewModel
class SaveViewModel @Inject constructor(
    app: Application
) : BaseViewModel(app){

    val onSavePercentageChange = MutableLiveData<Int>()

    val onDownloadFailed = MutableLiveData<String>()
    val onDownloadCompleted = MutableLiveData<Pair<Uri?, String?>>()

    private var isSaveFailed = false
    private var savedFileUri: Uri? = null

    fun moveFile(activity: FragmentActivity, inputPath: String, inputFile: String, documentFile: DocumentFile?) {
        if (documentFile != null) {
            var inputStream: InputStream? = null
            var out: OutputStream? = null
            try {
                inputStream = FileInputStream(inputPath + inputFile)
                val destinationFile: DocumentFile? = documentFile.createFile("", inputFile)
                out = destinationFile?.uri?.let { activity.contentResolver.openOutputStream(it) }

                val file = File(inputPath + inputFile)
                val totalSize = file.length()
                val buffer = ByteArray(1024)
                var length: Int
                var percent: Float
                var downloaded = 0f
                while (inputStream.read(buffer).also { length = it } != -1) {
                    out?.write(buffer, 0, length)
                    downloaded += length
                    totalSize.let { percent = (downloaded * 100 / it) }

                    onSavePercentageChange.postValue(percent.toInt())
                }
                savedFileUri = destinationFile?.uri
            } catch (e: FileNotFoundException) {
                isSaveFailed = true
                onDownloadFailed.postValue(e.localizedMessage)
            } catch (e: java.lang.Exception) {
                isSaveFailed = true
                onDownloadFailed.postValue(e.localizedMessage)
            } finally {
                inputStream?.close()
                out?.flush()
                out?.close()

                // delete the original file
                File(inputPath + inputFile).delete()

                if (!isSaveFailed) {
                    onDownloadCompleted.postValue(Pair(savedFileUri, documentFile.name))
                }
                isSaveFailed = false
            }
        }
        else onDownloadFailed.postValue("Save destination not found")
    }

    fun navigateToFailureFragmentFromSaveFragment(error: String) {
        navigate(MoveToLocationFragmentDirections.actionMoveToLocationFragmentToDownloadFailureFragment(error))
    }

    fun navigateToSuccessFragment(uri: Uri?) {
        navigate(MoveToLocationFragmentDirections.actionMoveToLocationFragmentToDownloadSuccessFragment(uri?.toString()))
    }

}