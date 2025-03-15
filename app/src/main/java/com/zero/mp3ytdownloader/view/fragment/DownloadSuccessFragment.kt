package com.zero.mp3ytdownloader.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.navigation.fragment.navArgs
import com.zero.mp3ytdownloader.base.view.BaseFragment
import com.zero.mp3ytdownloader.databinding.FragmentDownloadSuccessBinding
import com.zero.mp3ytdownloader.view.activity.MainActivity
import java.io.File


class DownloadSuccessFragment : BaseFragment() {

    private lateinit var binding: FragmentDownloadSuccessBinding
    private val args: DownloadSuccessFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDownloadSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = File(args.uri.toString())

        binding.tvBack.setOnClickListener {
            (requireActivity() as MainActivity).popBackStack()
        }

        binding.rlOpenFile.setOnClickListener {
            openFile(requireContext(), file)
        }
    }

    private fun openFile(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)

        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, getMimeType(file))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            // No app found to open the file
            e.printStackTrace()
        }
    }

    private fun getMimeType(file: File): String? {
        val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
    }

}