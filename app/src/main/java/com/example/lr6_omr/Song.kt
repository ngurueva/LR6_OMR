package com.example.lr6_omr

import java.io.Serializable

data class Song(var title: String, var artist: String, var album: String, var isFavorite: Boolean) : Serializable  {
    override fun toString(): String {
        return "$title - $artist"
    }
}
