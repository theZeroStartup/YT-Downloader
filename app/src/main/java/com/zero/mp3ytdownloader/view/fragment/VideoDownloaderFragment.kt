package com.zero.mp3ytdownloader.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zero.mp3ytdownloader.base.view.BaseFragment
import com.zero.mp3ytdownloader.databinding.FragmentVideoDownloaderBinding
import com.zero.mp3ytdownloader.model.MediaDetails
import com.zero.mp3ytdownloader.viewmodel.DownloaderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoDownloaderFragment : BaseFragment() {

    private lateinit var binding: FragmentVideoDownloaderBinding
    private lateinit var downloaderViewModel: DownloaderViewModel

    private val args: VideoDownloaderFragmentArgs by navArgs()
    private lateinit var mediaDetails: MediaDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloaderViewModel = ViewModelProvider(this@VideoDownloaderFragment)[DownloaderViewModel::class.java]
        viewModel = downloaderViewModel

        lifecycleScope.launch {
            attachObservers()
        }

        val typeData = object : TypeToken<MediaDetails>() {}.type
        mediaDetails = Gson().fromJson(args.details, typeData) ?: MediaDetails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVideoDownloaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startDownload()
    }

    private var fileName = ""
    private fun startDownload() {
        if (this::mediaDetails.isInitialized) {
            createFile(mediaDetails.title.toString(), ".mp3") { file, fileName ->
                this.fileName = fileName
                downloaderViewModel.download(mediaDetails.url.toString(), file)
            }
        }
    }

    private fun attachObservers() {
        downloaderViewModel.onDownloadPercentageChange.observe(this) {
            binding.downloadProgress.progress = it.first
            binding.tvDownloadProgress.text = it.second
        }

        downloaderViewModel.onDownloadFailed.observe(this) {
            downloaderViewModel.navigateToFailureFragment(it)
        }

        downloaderViewModel.onDownloadCompleted.observe(this) {
            downloaderViewModel.navigateToConverterFragment(fileName.ifEmpty { mediaDetails.title.toString() }, it)
        }
    }
}