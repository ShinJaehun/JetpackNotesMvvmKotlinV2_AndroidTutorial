package com.shinjaehun.jetpacknotesmvvmkotlinv2.common

import android.text.Editable
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.FirebaseNote
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.Note
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.RoomNote
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.User
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun <T> awaitTaskResult(task: Task<T>): T = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            continuation.resume(task.result!!)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

internal suspend fun <T> awaitTaskCompletable(task: Task<T>): Unit = suspendCoroutine { continuation ->
    task.addOnCompleteListener { task ->
        if (task.isSuccessful) {
           continuation.resume(Unit)
        } else {
            continuation.resumeWithException(task.exception!!)
        }
    }
}

internal val FirebaseUser.toUser: User
    get() = User(
        uid = this.uid,
        name = this.displayName ?: ""
    )

internal val FirebaseNote.toNote: Note
    get() = Note(
        this.creationDate ?: "",
        this.contents ?: "",
        this.upVotes ?: 0,
        this.imageUrl ?: "",
        User(this.creator ?: "")
    )

internal val Note.toFirebaseNote: FirebaseNote
    get() = FirebaseNote(
        this.creationDate,
        this.contents,
        this.upVotes,
        this.imageUrl,
        this.safeGetUid
    )

internal val RoomNote.toNote: Note
    get() = Note(
        this.creationDate,
        this.contents,
        this.upVotes,
        this.imageUrl,
        User(this.creatorId)
    )

internal val Note.toRoomNote: RoomNote
    get() = RoomNote(
        this.creationDate,
        this.contents,
        this.upVotes,
        this.imageUrl,
        this.safeGetUid
    )

internal fun List<RoomNote>.toNoteListFromRoomNote(): List<Note> = this.flatMap {
    listOf(it.toNote)
}

internal fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

internal val Note.safeGetUid: String
    get() = this.creator?.uid ?: ""