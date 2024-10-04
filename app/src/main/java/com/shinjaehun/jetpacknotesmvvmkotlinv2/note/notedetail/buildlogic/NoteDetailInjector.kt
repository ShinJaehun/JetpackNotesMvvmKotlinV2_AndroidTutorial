package com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notedetail.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.RoomNoteDatabase
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.implementations.NoteRepoImpl
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository.INoteRepository

class NoteDetailInjector(application: Application): AndroidViewModel(application) {

    private fun getNoteRepository(): INoteRepository {
        FirebaseApp.initializeApp(getApplication())
        return NoteRepoImpl(
            local = RoomNoteDatabase.getInstance(getApplication()).roomNoteDao()
        )
    }

    fun provideNoteViewModelFactory(): NoteViewModelFactory =
        NoteViewModelFactory(getNoteRepository())
}