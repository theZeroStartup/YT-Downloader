package com.zero.mp3ytdownloader.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.zero.mp3ytdownloader.base.view.BaseFragment
import com.zero.mp3ytdownloader.databinding.FragmentDownloadFailureBinding
import com.zero.mp3ytdownloader.view.activity.MainActivity

class DownloadFailureFragment : BaseFragment() {

    private lateinit var binding: FragmentDownloadFailureBinding
    private val args: DownloadFailureFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDownloadFailureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvError.text = args.error

        binding.rlDownload.setOnClickListener {
            (requireActivity() as MainActivity).popBackStack()
        }
    }

}