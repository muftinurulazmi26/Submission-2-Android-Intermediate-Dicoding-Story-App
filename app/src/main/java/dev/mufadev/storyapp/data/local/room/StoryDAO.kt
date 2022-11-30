package dev.mufadev.storyapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.mufadev.storyapp.data.local.entity.Story

@Dao
interface StoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<Story>)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, Story>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}