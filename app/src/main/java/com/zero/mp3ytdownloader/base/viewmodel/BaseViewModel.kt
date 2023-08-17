package com.zero.mp3ytdownloader.base.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import com.zero.mp3ytdownloader.base.primarynav.NavigationCommand

abstract class BaseViewModel(app: Application) : AndroidViewModel(app){
    private val _pageProgressCommands: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val pageProgressCommands: LiveData<Boolean>
        get() = _pageProgressCommands


    private val _navigationCommands: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val navigationCommands: LiveData<NavigationCommand>
        get() = _navigationCommands

    fun navigate(directions: NavDirections){
        _navigationCommands.postValue(NavigationCommand.ToDirections(directions))
    }

    fun navigate(destinationId: Int){
        _navigationCommands.postValue(NavigationCommand.ToDestinationId(destinationId))
    }

    fun navigateBack(){
        _navigationCommands.postValue(NavigationCommand.Back)
    }

    fun navigateBackTo(destinationId: Int, isInclusive: Boolean){
        _navigationCommands.postValue(NavigationCommand.BackTo(destinationId, isInclusive))
    }

    fun navigate(deepLinkUri: Uri){
        _navigationCommands.postValue(NavigationCommand.ToDeepLink(deepLinkUri))
    }
}