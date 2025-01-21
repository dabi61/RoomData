package com.example.roomdata.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.roomdata.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM noteTable")
    fun getAllNote() : MutableList<Note>

    @Insert
    fun insertNote(note: Note)

    @Query("SELECT * FROM noteTable WHERE id = :id")
    fun getNoteById(id: Int): Note

    @Query("SELECT id FROM noteTable ORDER BY id DESC LIMIT 1")
    fun getLastId(): Int

    @Query("SELECT * FROM noteTable WHERE title LIKE '%' || :title || '%' OR content LIKE '%' || :content || '%'")
    fun getListNote(title: String, content: String): MutableList<Note>

    @Query("UPDATE noteTable SET title = :title, content = :content, editTime = :editTime WHERE id = :id")
    fun updateNote(id: Int, title: String, content: String, editTime: Long)


    @Query("Delete FROM noteTable WHERE id = :id")
    fun deleteNote(id: Int)


}


//CRUD on here