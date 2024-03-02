package com.example.roomjetpackcompose.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomjetpackcompose.data.Note
import com.example.roomjetpackcompose.data.NotesDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(
    private val dao: NotesDao
) : ViewModel() {
    private val isShortedByDateAdded = MutableStateFlow(true)
    private val notes = isShortedByDateAdded.flatMapLatest { short ->
        if (short) {
            dao.getNotesOrderByDateAdded()
        } else {
            dao.getNotesOrderByTitle()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    val _state = MutableStateFlow(NoteState())
    val state = combine(_state, isShortedByDateAdded, notes) { state, isShortedByDateAdded, notes ->
        state.copy(
            notes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteState())

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    dao.delete(event.note)
                }
            }

            is NotesEvent.SaveNote -> {
                val note = Note(
                    title = state.value.title.value,
                    description = state.value.description.value,
                    dateAdded = System.currentTimeMillis()
                )
                viewModelScope.launch {
                    dao.upsertNote(note)
                }
                _state.update {
                    it.copy(
                        title = mutableStateOf(""),
                        description = mutableStateOf("")
                    )
                }
            }

            NotesEvent.ShortNotes -> {
                isShortedByDateAdded.value = !isShortedByDateAdded.value
            }
        }
    }
}