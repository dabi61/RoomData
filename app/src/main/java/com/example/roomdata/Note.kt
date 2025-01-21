package com.example.roomdata


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "noteTable")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String?,
    val content: String?,
    val createTime: Long,
    val editTime: Long?,
    )