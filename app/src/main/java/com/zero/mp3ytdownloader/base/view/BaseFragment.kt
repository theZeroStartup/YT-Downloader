package com.zero.mp3ytdownloader.base.view

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.View
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.zero.mp3ytdownloader.base.primarynav.NavigationCommand
import com.zero.mp3ytdownloader.base.viewmodel.BaseViewModel
import java.io.File

abstract class BaseFragment: Fragment() {

    protected lateinit var viewModel: BaseViewModel
    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        if (this::viewModel.isInitialized) {
            viewModel.navigationCommands.observe(viewLifecycleOwner) { command ->
                when (command) {
                    is NavigationCommand.ToDirections -> navController.navigate(command.direction)
                    is NavigationCommand.ToDestinationId -> navController.navigate(command.destinationId)
                    is NavigationCommand.BackTo -> navController.popBackStack(
                        command.destinationId,
                        command.isInclusive
                    )
                    is NavigationCommand.Back -> navController.popBackStack()
                    is NavigationCommand.ToDeepLink -> navController.navigate(command.deepLinkUri)
                    else -> {}
                }
            }
        }
    }

    fun getDestinationFile() : DocumentFile? {
        requireActivity().let {
            if (it is BaseActivity) return it.getDestinationFile()
        }
        return null
    }

    fun getDirectory(): String {
        requireActivity().let {
            if (it is BaseActivity) return it.getDirectory()?.path.toString()
        }
        return ""
    }

    fun createFile(prefix: String, extension: String, onFileCreated: (File, String) -> Unit) {
        requireActivity().let {
            if (it is BaseActivity) it.createFile(prefix, extension, onFileCreated)
        }
    }

    fun createDocumentFile(prefix: String, extension: String, onFileCreated: (File, String) -> Unit) {
        requireActivity().let {
            if (it is BaseActivity) it.createDocumentFile(prefix, extension, onFileCreated)
        }
    }

    fun showToast(message: String) {
        requireActivity().let {
            if (it is BaseActivity) it.showToast(message)
        }
    }

    fun getPath(context: Context?, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(requireContext(), contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return contentUri?.let {
                    getDataColumn(requireContext(), it, selection, selectionArgs)
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                requireContext(),
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }
}