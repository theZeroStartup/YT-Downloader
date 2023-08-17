package com.zero.mp3ytdownloader.model

import com.google.gson.annotations.SerializedName

data class MediaDetails(
    @SerializedName("like_count")
    var likesCount: String? = null,
    @SerializedName("view_count")
    var viewsCount: String? = null,
    @SerializedName("thumbnail")
    var thumbnail: String? = null,
    @SerializedName("url")
    var url: String? = null,
    @SerializedName("title")
    var title: String? = null,

    )
