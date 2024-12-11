package com.example.lr6_omr

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongTable(
    @PrimaryKey(autoGenerate = true) val idSong: Int = 0,
    @ForeignKey(
        entity = AlbumTable::class,
        parentColumns = ["idAlbum"],
        childColumns = ["albumId"],
        onDelete = ForeignKey.CASCADE
    )
    val albumIdAlbum: Int,
    @ForeignKey(
        entity = AuthorTable::class,
        parentColumns = ["idAuthor"],
        childColumns = ["authorId"],
        onDelete = ForeignKey.CASCADE
    )
    val authorIdAuthor: Int,
    val title: String,
    val isFavorite: Boolean
)

