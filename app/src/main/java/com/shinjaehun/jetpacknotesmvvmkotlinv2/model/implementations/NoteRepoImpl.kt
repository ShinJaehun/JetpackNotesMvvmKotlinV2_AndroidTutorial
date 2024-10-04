package com.shinjaehun.jetpacknotesmvvmkotlinv2.model.implementations

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.Result
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.awaitTaskCompletable
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.awaitTaskResult
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.toFirebaseNote
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.toNote
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.toNoteListFromRoomNote
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.toRoomNote
import com.shinjaehun.jetpacknotesmvvmkotlinv2.common.toUser
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.FirebaseNote
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.Note
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.NoteDao
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.User
import com.shinjaehun.jetpacknotesmvvmkotlinv2.model.repository.INoteRepository

private const val COLLECTION_NAME = "notes"

class NoteRepoImpl(
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    val remote: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val local: NoteDao
) : INoteRepository {
    override suspend fun getNoteById(noteId: String): Result<Exception, Note> {
        val user = getActiveUser()
        return if (user != null) getRemoteNote(noteId, user)
        else getLocalNote(noteId)
    }

    override suspend fun deleteNote(note: Note): Result<Exception, Unit> {
        val user = getActiveUser()
        return if (user != null) deleteRemoteNote(note.copy(creator = user))
        else deleteLocalNote(note)
    }

    override suspend fun updateNote(note: Note): Result<Exception, Unit> {
        val user = getActiveUser()
        return if (user != null) updateRemoteNote(note.copy(creator = user))
        else updateLocalNote(note)
    }

    override suspend fun getNotes(): Result<Exception, List<Note>> {
        val user = getActiveUser()
        return if (user != null) getRemoteNotes(user)
        else getLocalNotes()
    }

    private fun getActiveUser(): User? {
        return firebaseAuth.currentUser?.toUser
    }

    private suspend fun getRemoteNotes(user: User): Result<Exception, List<Note>> {
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .whereEqualTo("creator", user.uid)
                    .get()
            )

            resultToNoteList(task)
        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    private fun resultToNoteList(result: QuerySnapshot?): Result<Exception, List<Note>> {
        val noteList = mutableListOf<Note>()

        result?.forEach { documentSnapshot ->
            noteList.add(documentSnapshot.toObject(FirebaseNote::class.java).toNote)
        }

        return Result.build { noteList }
    }

    /**
     * Notes are stored with the following composite document name:
     * note.creationDate + note.creator.uid
     * The reason for this, is that if I just used the creationDate, hypothetically two users
     * creating a note at the same time, would have duplicate entries in the cloud database :(
     */
    private suspend fun getRemoteNote(creationDate: String, user: User): Result<Exception, Note> {
        //creationDate가 noteId임!
        return try {
            val task = awaitTaskResult(
                remote.collection(COLLECTION_NAME)
                    .document(creationDate + user.uid) // 이게 정확히 뭘까? // 이제 알겠지?
                    .get()
            )

            Result.build {
                // task가 이런 Task<DocumentSnapshot!> 유형이라서... object로 변경해줘야 하는겨?
                task.toObject(FirebaseNote::class.java)?.toNote ?: throw Exception()
            }
        } catch (exception: Exception) {
            Result.build { throw exception }

        }
    }

    private suspend fun deleteRemoteNote(note: Note): Result<Exception, Unit> = Result.build {
        awaitTaskCompletable(
            remote.collection(COLLECTION_NAME)
                .document(note.creationDate + note.creator!!.uid)
                .delete()
        )
    }

    private suspend fun updateRemoteNote(note: Note): Result<Exception, Unit> {
        return try {
            awaitTaskCompletable(
                remote.collection(COLLECTION_NAME)
                    .document(note.creationDate + note.creator!!.uid)
                    .set(note.toFirebaseNote)
            )
            Result.build { Unit }
        } catch (exception: Exception) {
            Result.build { throw exception }
        }
    }

    private suspend fun getLocalNotes(): Result<Exception, List<Note>> = Result.build {
        local.getNotes().toNoteListFromRoomNote()
    }

    private suspend fun getLocalNote(id: String): Result<Exception, Note> = Result.build {
        local.getNoteById(id).toNote
    }

    private suspend fun deleteLocalNote(note: Note): Result<Exception, Unit> = Result.build {
        local.deleteNote(note.toRoomNote)
        Unit
    }

    private suspend fun updateLocalNote(note: Note): Result<Exception, Unit> = Result.build {
        local.insertOrUpdateNote(note.toRoomNote)
        Unit
    }
}