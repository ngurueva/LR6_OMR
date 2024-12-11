package com.example.lr6_omr

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "authors")
data class AuthorTable(
    @PrimaryKey(autoGenerate = true) val idAuthor: Int,
    val name: String
)