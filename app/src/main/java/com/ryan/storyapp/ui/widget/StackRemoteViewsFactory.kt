package com.ryan.storyapp.ui.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.ryan.storyapp.R

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private var status = 0
    private var expectedImageCount = 0
    private val mWidgetItems = ArrayList<Bitmap>()

    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        status = 0
        expectedImageCount = 1
        mWidgetItems.clear()
        mWidgetItems.add(BitmapFactory.decodeResource(mContext.resources, R.drawable.test))

        Glide.with(mContext)
            .asBitmap()
            .load("https://story-api.dicoding.dev/images/stories/photos-1697971640233_Irsd9fzL.jpg")
            .encodeQuality(25)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    mWidgetItems.add(resource)
                    status++
                    if (status == expectedImageCount) {
                        updateWidget()
                    }
                }

                override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                    // Kosongkan
                }
            })
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            StoryImageWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(mContext)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(mContext, StoryImageWidget::class.java)
        )

        if (mWidgetItems.isNotEmpty()) {
            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(mContext.packageName, R.layout.story_image_widget)
                views.setImageViewBitmap(R.id.imageView, mWidgetItems.last())
                // appWidgetManager.updateAppWidget(appWidgetId, views) // -> this code error
            }
        }
    }
}