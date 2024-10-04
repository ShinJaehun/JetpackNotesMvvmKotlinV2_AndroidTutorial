package com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notedetail.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository.INoteRepository
import com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notedetail.NoteViewModel
import kotlinx.coroutines.Dispatchers

class NoteViewModelFactory(
    private val noteRepo: INoteRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(noteRepo, Dispatchers.Main) as T
    }
}