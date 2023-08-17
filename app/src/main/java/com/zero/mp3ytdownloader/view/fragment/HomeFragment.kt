package com.zero.mp3ytdownloader.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.zero.mp3ytdownloader.R
import com.zero.mp3ytdownloader.base.view.BaseFragment
import com.zero.mp3ytdownloader.databinding.FragmentHomeBinding
import com.zero.mp3ytdownloader.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.regex.Pattern


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this@HomeFragment)[HomeViewModel::class.java]
        viewModel = homeViewModel

        lifecycleScope.launch {
            attachObservers()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDestination.text = if (getDestinationFile() == null) "..." else getDestinationFile()?.name
        viewActionListeners()
    }

    private fun attachObservers() {
        homeViewModel.videoDetails.observe(this) {
            toggleViewState(true)
            binding.etLink.text?.clear()
            homeViewModel.navigateToDownloadFragment(it)
        }

        homeViewModel.extractionError.observe(this) {
            showToast("Failed to grab video info")
            toggleViewState(true)
        }
    }

    private fun viewActionListeners() {
        binding.etLink.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                val link = p0.toString()
                binding.ivClearText.visibility = if (link.isNotEmpty()) View.VISIBLE
                else View.INVISIBLE
            }
        })

        binding.ivClearText.setOnClickListener { binding.etLink.text?.clear() }

        binding.rlDownload.setOnClickListener {
            val link = binding.etLink.text.toString().trim()
            if (isValidInput(link)){
                toggleViewState(false)
                homeViewModel.grabVideoDetails(link)
            }
            else {
                showToast("Fill all fields with valid input")
                toggleViewState(true)
            }
        }

        binding.tvDestination.setOnClickListener {
            showDirectoryPicker()
        }
    }

    private fun showDirectoryPicker() {
        val i = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        directoryPickerResult.launch(Intent.createChooser(i, "Choose directory"))
    }

    private val directoryPickerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val uri = it.data?.data
            val documentFile = uri?.let { it1 -> DocumentFile.fromTreeUri(requireActivity(), it1) }
            val path: String = documentFile?.name.toString().trim()

            val takeFlags: Int? = (it.data?.flags?.and(
                (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)))

            if (uri != null && takeFlags != null) {
                requireActivity().contentResolver.takePersistableUriPermission(uri, takeFlags)
            }

            homeViewModel.saveStoragePath(uri.toString())
            binding.tvDestination.text = path
        }
    }

    private fun toggleViewState(isEnabled: Boolean) {
        binding.etLink.isEnabled = isEnabled
        binding.tvDestination.isEnabled = isEnabled
        binding.rlDownload.isEnabled = isEnabled
        binding.ivClearText.isEnabled = isEnabled
        binding.rlLinkRoot.isEnabled = isEnabled

        binding.etLink.isClickable = isEnabled
        binding.tvDestination.isClickable = isEnabled
        binding.rlDownload.isClickable = isEnabled
        binding.ivClearText.isClickable = isEnabled

        binding.tvDownload.text =
            if (!isEnabled) {
                binding.etLink.setTextColor(ResourcesCompat.getColor(resources, R.color.grey_disabled, null))
                binding.tvDestination.setTextColor(ResourcesCompat.getColor(resources, R.color.grey_disabled, null))
                binding.etLink.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.img_link_disabled,
                    0, 0, 0)
                binding.tvDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.img_destination_folder_disabled,
                    0, 0, 0)

                binding.progress.visibility = View.VISIBLE
                getString(R.string.grabbing_video)
            }
            else {
                binding.etLink.setTextColor(ResourcesCompat.getColor(resources, R.color.black_heading, null))
                binding.tvDestination.setTextColor(ResourcesCompat.getColor(resources, R.color.black_heading, null))
                binding.etLink.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.img_link_enabled,
                    0, 0, 0)
                binding.tvDestination.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.img_destination_folder_enabled,
                    0, 0, 0)

                binding.progress.visibility = View.GONE
                getString(R.string.download)
            }
    }

    private fun isValidInput(link: String): Boolean {
        val youtubePattern: Pattern =
            Pattern.compile("^(http(s)?://)?((w){3}.)?youtu(be|.be)?(.com)?/.+")
        val isValid: Boolean = youtubePattern.matcher(link).matches()
        val destination = binding.tvDestination.text.toString()

        return link.isNotEmpty() &&
                destination.replace("...", "").trim().isNotEmpty() && isValid
    }
}