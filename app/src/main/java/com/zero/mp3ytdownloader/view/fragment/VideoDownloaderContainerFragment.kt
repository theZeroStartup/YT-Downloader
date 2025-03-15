package com.zero.mp3ytdownloader.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zero.mp3ytdownloader.R
import com.zero.mp3ytdownloader.base.view.BaseFragment
import com.zero.mp3ytdownloader.databinding.FragmentVideoDownloaderContainerBinding
import com.zero.mp3ytdownloader.model.MediaDetails
import com.zero.mp3ytdownloader.util.CommonUtils.inReadableFormat
import com.zero.mp3ytdownloader.viewmodel.DownloaderContainerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoDownloaderContainerFragment : BaseFragment() {

    private lateinit var binding: FragmentVideoDownloaderContainerBinding
    private lateinit var downloaderViewModel: DownloaderContainerViewModel

    private val args: VideoDownloaderContainerFragmentArgs by navArgs()
    var mediaDetails: MediaDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloaderViewModel = ViewModelProvider(this@VideoDownloaderContainerFragment)[DownloaderContainerViewModel::class.java]
        viewModel = downloaderViewModel

        val typeData = object : TypeToken<MediaDetails>() {}.type
        mediaDetails = Gson().fromJson(args.details, typeData) ?: MediaDetails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVideoDownloaderContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        Glide.with(this)
            .load(mediaDetails?.thumbnail)
            .into(binding.ivThumbnail)

        binding.tvTitle.text = mediaDetails?.title
        binding.tvViewsCount.text = mediaDetails?.viewsCount.inReadableFormat()
        binding.tvLikesCount.text = mediaDetails?.likesCount.inReadableFormat()

        getMyNavController().setGraph(R.navigation.nav_graph_downloads,
            VideoDownloaderFragmentArgs(Gson().toJson(mediaDetails)).toBundle())
    }

    private fun getMyNavController(): NavController {
        return (getHostFragment() as NavHostFragment).navController
    }

    private fun getHostFragment(): Fragment? {
        return childFragmentManager.findFragmentById(R.id.downloadFragmentContainer)
    }
}
