package com.example.lr6_omr

import java.io.Serializable

data class Artist(val name: String) : Serializable  {
    override fun toString(): String {
        return "$name"
    }
}
