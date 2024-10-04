package com.shinjaehun.jetpacknotesmvvmkotlinv2.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    suspend fun getNotes(): List<RoomNote>

    @Query("SELECT * FROM notes WHERE creation_date = :creationDate")
    suspend fun getNoteById(creationDate: String): RoomNote

    @Delete
    suspend fun deleteNote(note: RoomNote)

    //if update successful, will return number of rows effected, which should be 1
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNote(note: RoomNote): Long
}