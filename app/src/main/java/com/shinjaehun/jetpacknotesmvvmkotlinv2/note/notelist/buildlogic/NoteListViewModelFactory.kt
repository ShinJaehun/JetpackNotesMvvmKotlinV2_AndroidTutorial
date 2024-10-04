package com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notelist.buildlogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository.INoteRepository
import com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notelist.NoteListViewModel
import kotlinx.coroutines.Dispatchers

class NoteListViewModelFactory(
    private val noteRepo: INoteRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteListViewModel(noteRepo, Dispatchers.Main) as T
    }
}