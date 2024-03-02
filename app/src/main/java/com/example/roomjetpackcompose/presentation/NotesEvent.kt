package com.example.roomjetpackcompose.presentation

import com.example.roomjetpackcompose.data.Note

sealed interface NotesEvent{
    object ShortNotes : NotesEvent
    data class DeleteNote(val note : Note) : NotesEvent
    data class SaveNote(
        val title : String,
        val description : String
    ) : NotesEvent
}