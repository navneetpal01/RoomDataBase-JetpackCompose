package com.example.roomjetpackcompose.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Upsert
    // @Insert was used to insert but in the newer versions we got Upsert which do both insert and update
    suspend fun upsertNote(note : Note)

    @Delete
    suspend fun delete(note : Note)

    @Query("SELECT * FROM note ORDER BY dateAdded")
    fun getNotesOrderByDateAdded() : Flow<List<Note>>

    @Query("SELECT * FROM note ORDER BY title ASC")
    fun getNotesOrderByTitle() : Flow<List<Note>>
}