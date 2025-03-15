package com.zero.mp3ytdownloader.base.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.IOException


abstract class BaseActivity: AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onResume() {
        super.onResume()
        if (!hasPermissions(this)){
            showRationale()
        }
    }

    private var num = 0
    fun createFile(prefix: String, extension: String, onFileCreated: (File, String) -> Unit) {
        try {
            val regex = Regex("[^A-Za-z0-9 ]")
            val title = regex.replace(prefix, "")
                .replace("  ", " ").trim()

            val fileName = if (num == 0) "$title$extension" else "$title ($num)$extension"
            val myFile = File(getDirectory(), fileName)
            if (!myFile.exists()) {
                myFile.createNewFile()
                val name = fileName.removeSuffix(extension)
                onFileCreated.invoke(myFile, name)
                num = 0
            } else {
                num++ //increase the file index
                createFile(prefix, extension, onFileCreated) //simply call this method again with the same prefix
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private var n = 0
    fun createDocumentFile(prefix: String, extension: String, onFileCreated: (File, String) -> Unit) {
        try {
            val regex = Regex("[^A-Za-z0-9 ]")
            val title = regex.replace(prefix, "")
                .replace("  ", " ").trim()

            val fileName = if (n == 0) "$title$extension" else "$title ($n)$extension"
            val myFile = File(getDirectory(), fileName)
            if (isFileNotExistAlready(fileName)) {
                myFile.createNewFile()
                val name = fileName.removeSuffix(extension)
                onFileCreated.invoke(myFile, name)
                n = 0
            } else {
                n++ //increase the file index
                createDocumentFile(prefix, extension, onFileCreated) //simply call this method again with the same prefix
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun isFileNotExistAlready(fileName: String): Boolean {
        val documentFile = getDestinationFile()
        return documentFile?.findFile(fileName) == null
    }

    fun getDirectory(): File? {
        var directory: File?
        directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/YT")
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/${Environment.DIRECTORY_DOWNLOADS}/YT")
        }

        if (!directory.exists()) {
            // Make it, if it doesn't exit
            val success = directory.mkdirs()
            if (!success) {
                directory = null
            }
        }
        return directory
    }

    fun getDestinationFile(): DocumentFile? {
        return if (getStoragePath()?.toString()?.isNotEmpty() == true) {
            getStoragePath()?.let { DocumentFile.fromTreeUri(this, it) }
        } else null
    }

    abstract fun getStoragePath(): Uri?

    private fun requestPermissions() {
        if (!hasPermissions(this) && Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    private fun hasPermissions(context: Context): Boolean {
        return hasPermissions(context, permissions)
    }

    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
        else {
            return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!hasPermissions(this))
            showRationale()
    }

    private fun showRationale() {
        AlertDialog.Builder(this)
            .setTitle("Storage Access Required")
            .setMessage("Please grant storage permission to continue")
            .setPositiveButton("Allow") { dialog, which ->
                requestPermissions()
            }
            .setNegativeButton("Exit") { _, _ ->
                finishAffinity()
            }
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}