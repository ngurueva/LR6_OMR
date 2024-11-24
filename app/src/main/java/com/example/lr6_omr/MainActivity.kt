package com.example.lr6_omr

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    var allSongs = mutableListOf<Song>()
    var favorites = mutableListOf<Song>()
    var albums = mutableListOf<Album>()
    var artists = mutableListOf<Artist>()
    val ALL_SONGS_REQUEST_CODE = 100
    private lateinit var songAdapter: MainSongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        buttonAdd.setOnClickListener { addSong() }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_favorites -> {
//                showFavorites()
                return true
            }
            R.id.action_show_all_songs -> {
                showAllSongs()
                return true
            }
            R.id.action_albums -> {
//                showAlbums()
                return true
            }
            R.id.action_artists -> {
//                showArtists()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    private fun showAllSongs() {
        val intent = Intent(this, AllSongsActivity::class.java)
        intent.putExtra("allSongs", allSongs as Serializable) // Pass the song list as Serializable
        startActivity(intent)
    }

    private fun addSong() {
        val titleEditText = findViewById<EditText>(R.id.editTextTitle)
        val artistEditText = findViewById<EditText>(R.id.editTextArtist)
        val albumEditText = findViewById<EditText>(R.id.editTextAlbum)
        val checkBoxFavorite = findViewById<CheckBox>(R.id.checkBoxFavorite)
        val isFavorite = checkBoxFavorite?.isChecked ?: false

        val title = titleEditText?.text.toString().trim()
        val artist = artistEditText?.text.toString().trim()
        val album = albumEditText?.text.toString().trim()

        if (title.isEmpty() || artist.isEmpty() || album.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        if (!albums.any { it.title == album && it.artist == artist }) {
            val newAlbum = Album(album, artist)
            albums.add(newAlbum)
        }

        if (!artists.any { it.name == artist }) {
            val newArtist = Artist(artist)
            artists.add(newArtist)
        }

        val newSong = Song(title, artist, album, isFavorite)
        allSongs.add(newSong)

        if (isFavorite) {
            favorites.add(newSong)
        }

        songAdapter.updateSongList(allSongs)


        titleEditText?.text?.clear()
        artistEditText?.text?.clear()
        albumEditText?.text?.clear()
        checkBoxFavorite?.isChecked = false
    }
}