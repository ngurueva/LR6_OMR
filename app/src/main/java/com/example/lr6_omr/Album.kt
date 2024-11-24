package com.example.lr6_omr

import java.io.Serializable

data class Album(val title: String, val artist: String) : Serializable  {
    override fun toString(): String {
        return "$title - $artist"
    }
}
