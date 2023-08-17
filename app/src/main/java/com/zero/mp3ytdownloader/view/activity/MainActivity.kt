package com.zero.mp3ytdownloader.view.activity

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.zero.mp3ytdownloader.R
import com.zero.mp3ytdownloader.base.view.BaseActivity
import com.zero.mp3ytdownloader.databinding.ActivityMainBinding
import com.zero.mp3ytdownloader.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(binding.root)
    }

    override fun getStoragePath(): Uri? {
        return viewModel.getStoragePath()
    }

    fun popBackStack() {
        getMyNavController().popBackStack()
    }

    private fun getMyNavController(): NavController {
        return (getHostFragment() as NavHostFragment).navController
    }

    private fun getHostFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.mainFragmentContainer)
    }
}