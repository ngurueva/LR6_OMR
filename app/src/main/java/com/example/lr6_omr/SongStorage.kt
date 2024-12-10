package com.example.lr6_omr

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SongStorage(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("song_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveSongs(songs: List<Song>) {
        val json = gson.toJson(songs)
        sharedPreferences.edit().putString("songs", json).apply()
    }

    fun loadSongs(): MutableList<Song> {
        val json = sharedPreferences.getString("songs", null)
        val type = object : TypeToken<List<Song>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }
}
