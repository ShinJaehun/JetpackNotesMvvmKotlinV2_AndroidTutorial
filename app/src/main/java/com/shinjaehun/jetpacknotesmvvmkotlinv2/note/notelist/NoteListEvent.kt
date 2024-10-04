package com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notelist

sealed class NoteListEvent {
    data class OnNoteItemClick(val position: Int) : NoteListEvent()
    object OnNewNoteClick : NoteListEvent()
    object OnStart : NoteListEvent()
}