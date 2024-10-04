package com.shinjaehun.jetpacknotesmvvmkotlinv2.note.notelist

import androidx.recyclerview.widget.DiffUtil
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.Note

class NoteDiffUtilCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.creationDate == newItem.creationDate
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.creationDate == newItem.creationDate
    }
}