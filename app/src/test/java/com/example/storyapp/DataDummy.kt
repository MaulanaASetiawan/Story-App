package com.example.storyapp

import com.example.storyapp.data.local.database.entity.StoryEntity

object DataDummy {

    fun generateDummyQuoteResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                id = i.toString(),
                photoUrl = "https://picsum.photos/200/300?random=$i",
                createdAt = "2024-10-24T12:00:00Z",
                name = "Author $i",
                description = "This is a description for story $i",
                lon = (Math.random() * 180) - 90,
                lat = (Math.random() * 180) - 90
            )
            items.add(story)
        }
        return items
    }
}