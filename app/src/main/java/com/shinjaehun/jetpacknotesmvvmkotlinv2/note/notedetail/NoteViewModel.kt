package com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notedetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.BaseViewModel
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.GET_NOTE_ERROR
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.Note
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository.INoteRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import kotlin.coroutines.CoroutineContext

private const val TAG = "NoteViewModel"

class NoteViewModel(
    val noteRepo: INoteRepository,
    uiContext: CoroutineContext
) : BaseViewModel<NoteDetailEvent>(uiContext) {

    private val noteState = MutableLiveData<Note>()
    val note: LiveData<Note> get() = noteState

    private val deletedState = MutableLiveData<Boolean>()
    val deleted: LiveData<Boolean> get() = deletedState

    private val updatedState = MutableLiveData<Boolean>()
    val updated: LiveData<Boolean> get() = updatedState

    override fun handleEvent(event: NoteDetailEvent) {
        when(event){
            is NoteDetailEvent.OnStart -> getNote(event.noteId)
            is NoteDetailEvent.OnDeleteClick -> onDelete()
            is NoteDetailEvent.OnDoneClick -> updateNote(event.contents)
            else -> {
                Log.i(TAG, "이게 출력되면 안 되는 걸로 아는디...")
            }
        }
    }

    private fun getNote(noteId: String) = launch {
        if (noteId == "") newNote()
        else {
            val noteResult = noteRepo.getNoteById(noteId)
            when (noteResult) {
                is Result.Value -> noteState.value = noteResult.value
                is Result.Error -> errorState.value = GET_NOTE_ERROR
            }
        }
    }

    private fun newNote() {
        noteState.value =
            Note(getCalendarTime(), "", 0, "rocket_loop", null)
    }

    private fun getCalendarTime(): String {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        val format = SimpleDateFormat("yyyy MMM d HH:mm:ss Z")
        format.timeZone = cal.timeZone
        return format.format(cal.time)
    }

    private fun updateNote(contents: String) = launch {
        val updateResult = noteRepo.updateNote(
            note.value!!.copy(contents = contents)
        )
        when(updateResult){
            is Result.Value -> updatedState.value = true
            is Result.Error -> updatedState.value = false
        }
    }

    private fun onDelete() = launch {
        val deleteResult = noteRepo.deleteNote(note.value!!)
        when(deleteResult){
            is Result.Value -> deletedState.value = true
            is Result.Error -> deletedState.value = false
        }
    }

}