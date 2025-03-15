package com.zero.mp3ytdownloader.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.zero.mp3ytdownloader.base.view.BaseFragment
import com.zero.mp3ytdownloader.databinding.FragmentMoveToLocationBinding
import com.zero.mp3ytdownloader.viewmodel.SaveViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoveToLocationFragment : BaseFragment() {

    private lateinit var binding: FragmentMoveToLocationBinding
    private lateinit var downloaderViewModel: SaveViewModel

    private val args: MoveToLocationFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        downloaderViewModel = ViewModelProvider(this@MoveToLocationFragment)[SaveViewModel::class.java]
        viewModel = downloaderViewModel

        lifecycleScope.launch {
            attachObservers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMoveToLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filePath = getDirectory()
        val fileName = args.path.removePrefix(filePath).trim()
        downloaderViewModel.moveFile(requireActivity(), filePath, fileName, getDestinationFile())
    }

    private fun attachObservers() {
        downloaderViewModel.onSavePercentageChange.observe(this) {
            binding.downloadProgress.progress = it
            binding.tvDownloadProgress.text = it.toString().plus("%")
        }

        downloaderViewModel.onDownloadFailed.observe(this) {
            downloaderViewModel.navigateToFailureFragmentFromSaveFragment(it)
        }

        downloaderViewModel.onDownloadCompleted.observe(this) {
            showToast("File saved to ${it.second?.ifEmpty { getDirectory() }}")
            downloaderViewModel.navigateToSuccessFragment(it.first)
        }
    }
}