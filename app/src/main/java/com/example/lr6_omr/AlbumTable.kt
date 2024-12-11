package com.example.lr6_omr

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumTable(
    @PrimaryKey(autoGenerate = true) val idAlbum: Int,
    val title: String
)