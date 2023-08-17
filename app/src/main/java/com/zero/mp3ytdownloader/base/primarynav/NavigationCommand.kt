package com.zero.mp3ytdownloader.base.primarynav

import android.net.Uri
import androidx.navigation.NavDirections

sealed class NavigationCommand {

    data class ToDirections(val direction: NavDirections) : NavigationCommand()

    data class ToDestinationId(val destinationId: Int) : NavigationCommand()

    object Back : NavigationCommand()

    data class BackTo(val destinationId: Int, val isInclusive: Boolean) : NavigationCommand()

    object ToRoot : NavigationCommand()

    data class ToDeepLink(val deepLinkUri: Uri) : NavigationCommand()

}