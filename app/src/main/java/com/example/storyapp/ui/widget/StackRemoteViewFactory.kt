package com.example.storyapp.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.storyapp.R
import com.example.storyapp.data.Result
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.remote.response.ListStoryItem
import com.example.storyapp.di.Injection
import kotlinx.coroutines.runBlocking

class StackRemoteViewsFactory(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {
    private var storyItems = mutableListOf<ListStoryItem>()
    private lateinit var repository: StoryRepository

    override fun onCreate() {
        repository = Injection.provideRepository(mContext)
        onDataSetChanged()
    }

    override fun onDataSetChanged() {
        runBlocking {
            val result = repository.getStoriesForWidget()
            result.let {
                when (it) {
                    is Result.Success -> {
                        storyItems.clear()
                        it.data?.filterNotNull()?.let { nonNullList ->
                            storyItems.addAll(nonNullList)
                        }
                    }

                    is Result.Error -> {
                        storyItems.clear()
                    }
                    else -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        storyItems.clear()
    }

    override fun getCount(): Int = storyItems.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION || storyItems.isEmpty()) {
            return null
        }

        val story = storyItems[position]
        val views = RemoteViews(mContext.packageName, R.layout.item_widget)


        val imageUrl = story.photoUrl
        val bitmap = imageUrl?.let { getBitmapFromUrl(it) }

        if (bitmap != null) {
            views.setImageViewBitmap(R.id.imageItemWidget, bitmap)
        } else {
            views.setImageViewResource(R.id.imageItemWidget, R.drawable.ic_baseline_person_24)
        }

        return views
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(mContext.packageName, R.layout.item_widget)
    }

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = java.net.URL(imageUrl)
            val connection = url.openConnection()
            connection.doInput = true
            connection.connect()
            val input = connection.getInputStream()
            BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
