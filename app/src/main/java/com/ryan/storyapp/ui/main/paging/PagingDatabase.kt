package com.ryan.storyapp.ui.main.paging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ryan.storyapp.data.model.ListStoryItem
import com.ryan.storyapp.data.model.Stories
import com.ryan.storyapp.data.model.StoriesDetailResponse

@Database(
    entities = [ListStoryItem::class],
    version = 1,
    exportSchema = false
)
abstract class PagingDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: PagingDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): PagingDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PagingDatabase::class.java, "quote_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}