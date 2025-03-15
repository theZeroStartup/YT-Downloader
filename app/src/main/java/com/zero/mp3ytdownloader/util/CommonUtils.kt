package com.zero.mp3ytdownloader.util

import java.text.DecimalFormat

object CommonUtils {
    fun formatFileSize(size: Long): String {
        val hrSize: String?
        val b = size.toDouble()
        val k = size / 1024.0
        val m = size / 1024.0 / 1024.0
        val g = size / 1024.0 / 1024.0 / 1024.0
        val t = size / 1024.0 / 1024.0 / 1024.0 / 1024.0
        val dec = DecimalFormat("0.00")
        hrSize = if (t > 1) {
            dec.format(t).plus(" TB")
        } else if (g > 1) {
            dec.format(g).plus(" GB")
        } else if (m > 1) {
            dec.format(m).plus(" MB")
        } else if (k > 1) {
            dec.format(k).plus(" KB")
        } else {
            dec.format(b).plus(" Bytes")
        }
        return hrSize
    }

    fun String?.inReadableFormat(): String {
        val number = this?.toDouble()
        if (number != null) {
            when {
                number >= 1000000000 -> {
                    return String.format("%.2fB", number / 1000000000.0);
                }
                number >= 1000000 -> {
                    return String.format("%.2fM", number / 1000000.0);
                }
                number >= 100000 -> {
                    return String.format("%.2fL", number / 100000.0);
                }
                number >= 1000 -> {
                    return String.format("%.2fK", number / 1000.0);
                }
            }
        }
        return this.toString()
    }
}