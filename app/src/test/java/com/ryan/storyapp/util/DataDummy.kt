package com.ryan.storyapp.util

import com.ryan.storyapp.data.model.ListStoryItem

object DataDummy {

    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val listStoryItem = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1699034386087_XKcFo_Xa.jpg",
                "2023-11-03T17:59:46.089Z",
                "Rayi Entapayong",
                "tireddd",
                0.0,
                "story-$i",
                0.0
            )
            items.add(listStoryItem)
        }
        return items
    }
}