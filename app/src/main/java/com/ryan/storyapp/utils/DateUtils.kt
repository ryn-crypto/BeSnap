package com.ryan.storyapp.utils

import android.content.Context
import com.ryan.storyapp.R
import com.ryan.storyapp.databinding.ActivityDetailStoryBinding
import java.time.Instant

class DateUtils {

    companion object {
        internal fun calculateTimeAgo(context: Context, createdAt: String?): String {
            val createdTime = Instant.parse(createdAt).toEpochMilli()
            val currentTime = System.currentTimeMillis()
            val difference = currentTime - createdTime

            val seconds = difference / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                days > 0 -> "$days ${context.getString(R.string.day)}"
                hours > 0 -> "$hours ${context.getString(R.string.hour)}"
                minutes > 0 -> "$minutes ${context.getString(R.string.minute)}"
                else -> "$seconds ${context.getString(R.string.seconds)}"
            }
        }

    }
}