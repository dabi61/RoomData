package com.example.roomdata.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomdata.Constant.DATABASE_NAME
import com.example.roomdata.Note


@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: NoteDatabase? = null
        fun getInstance(context: Context): NoteDatabase {
            return instance ?: synchronized(this) {
                val instanceCreate =
                    Room.databaseBuilder(
                        context,
                        NoteDatabase::class.java,
                        DATABASE_NAME
                    )
                        .createFromAsset(DATABASE_NAME)
                        .build()
                instance = instanceCreate
                instance!!
            }
        }
    }
    abstract fun getNodeDao(): NoteDao
    // sửa
}