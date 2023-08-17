package com.zero.mp3ytdownloader.base.primarynav

import kotlin.math.min

class PrimaryNavModel (val segment: String, private val allNavItemModels: List<PrimaryNavItemModel>) {
    private lateinit var navItemModels: List<PrimaryNavItemModel>
    private var moreItemModels: List<PrimaryNavItemModel>? = null
    private var maxNavSize: Int? = null

    private fun getNavItems(maxNavItems: Int): List<PrimaryNavItemModel> {
        if (!::navItemModels.isInitialized || maxNavSize != maxNavItems){
            initNavItems(maxNavItems)
        }
        return navItemModels
    }

    fun getMoreItems(maxNavItems: Int): List<PrimaryNavItemModel>? {
        if (moreItemModels.isNullOrEmpty() || maxNavSize != maxNavItems){

        }
        return moreItemModels
    }

    private fun initNavItems(maxNavItems: Int){
        navItemModels = if (allNavItemModels.isEmpty()) {
            allNavItemModels
        } else{
            val itemCount = min(allNavItemModels.size, maxNavItems)
            allNavItemModels.subList(0, itemCount)
        }
    }

    private fun initMoreItems(maxNavItems: Int){
        val moreItemCount = allNavItemModels.size - getNavItems(maxNavItems).size
        if (moreItemCount > 0){
            moreItemModels = allNavItemModels.subList(getNavItems(maxNavItems).size, allNavItemModels.size)
        }
    }
}