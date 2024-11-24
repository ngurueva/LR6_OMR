package com.example.lr6_omr

import java.io.Serializable

data class Song(val title: String, val artist: String, val album: String, val isFavorite: Boolean) : Serializable  {
    override fun toString(): String {
        return "$title - $artist"
    }
}
