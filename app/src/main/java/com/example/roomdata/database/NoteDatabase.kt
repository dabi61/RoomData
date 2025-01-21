package com.example.roomdata.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.roomdata.Note


@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "note_db"
    }
    abstract fun getNodeDao(): NoteDao

}