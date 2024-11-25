package com.example.lr6_omr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable
import androidx.recyclerview.widget.ItemTouchHelper
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {
    var allSongs = mutableListOf<Song>()
    var favorites = mutableListOf<Song>()
    var albums = mutableListOf<Album>()
    var artists = mutableListOf<Artist>()
    val ALL_SONGS_REQUEST_CODE = 100
    private lateinit var recyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter

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

        recyclerView = findViewById(R.id.recyclerViewMain)
        recyclerView.layoutManager = LinearLayoutManager(this)
        songAdapter = SongAdapter(this, allSongs)
        recyclerView.adapter = songAdapter


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false

    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
         val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val songToRemove = allSongs[position]
                    allSongs.removeAt(position)
                    if (songToRemove.isFavorite) {
                        favorites.remove(songToRemove)
                    }
                    songAdapter.notifyItemRemoved(position)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_favorites -> {
                showFavorites()
                return true
            }
            R.id.action_show_all_songs -> {
                showAllSongs()
                return true
            }
            R.id.action_albums -> {
                showAlbums()
                return true
            }
            R.id.action_artists -> {
                showArtists()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun saveData() {
        saveDataToCSV(this)
        saveToPDF(this)
        saveToSharedPreferences(this)
    }

    fun saveDataToCSV(context: Context) {
        val csvFile = File(context.filesDir, "music_data.csv")
        FileWriter(csvFile).use { writer ->
            writer.appendLine("№;name;author;album;isFavorite")
            for ((index, song) in allSongs.withIndex()) {
                writer.appendLine("${index + 1};${song.title};${song.artist};${song.album};${song.isFavorite}")
            }
        }
    }

    fun saveToPDF(context: Context) {

    }

//    fun saveToSharedPreferences(context: Context) {
//
//    }



    fun saveToSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MusicData", Context.MODE_PRIVATE)
        val gson = Gson()
        val allSongsJson = gson.toJson(allSongs)
        val albumsJson = gson.toJson(albums)
        val artistsJson = gson.toJson(artists)
        val favoritesJson = gson.toJson(favorites)

        with (sharedPreferences.edit()) {
            putString("allSongs", allSongsJson)
            putString("albums", albumsJson)
            putString("artists", artistsJson)
            putString("favorites", favoritesJson)
            apply()
        }
        Toast.makeText(context, "Данные сохранены в Shared Preferences", Toast.LENGTH_SHORT).show()
    }


//    private fun loadDataFromSharedPreferences(context: Context) {
//        val sharedPreferences = context.getSharedPreferences("MusicData", Context.MODE_PRIVATE)
//        val gson = Gson()
//        val typeAllSongs = object : TypeToken<MutableList<Song>>() {}.type
//        val typeAlbums = object : TypeToken<MutableList<Album>>() {}.type
//        val typeArtists = object : TypeToken<MutableList<Artist>>() {}.type
//        val typeFavorites = object : TypeToken<MutableList<Song>>() {}.type
//
//
//        allSongs = gson.fromJson(sharedPreferences.getString("allSongs", "[]"), typeAllSongs) ?: mutableListOf()
//        albums = gson.fromJson(sharedPreferences.getString("albums", "[]"), typeAlbums) ?: mutableListOf()
//        artists = gson.fromJson(sharedPreferences.getString("artists", "[]"), typeArtists) ?: mutableListOf()
//        favorites = gson.fromJson(sharedPreferences.getString("favorites", "[]"), typeFavorites) ?: mutableListOf()
//
//        songAdapter.updateSongs(allSongs)
//    }



    private fun showAllSongs() {
        val intent = Intent(this, AllSongsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("allSongs", allSongs as Serializable)
        intent.putExtra("allSongsBundle", bundle)
        startActivity(intent)

        saveData()
    }

    private fun showFavorites() {
        val intent = Intent(this, FavoritesActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("favorites", favorites as Serializable)
        intent.putExtra("favoritesBundle", bundle)
        startActivity(intent)
    }

    private fun showAlbums() {
        val intent = Intent(this, AlbumsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("albums", albums as Serializable)
        intent.putExtra("albumsBundle", bundle)
        startActivity(intent)
    }
    private fun showArtists() {
        val intent = Intent(this, ArtistsActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("artists", artists as Serializable)
        intent.putExtra("artistsBundle", bundle)
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

        songAdapter.updateSongs(allSongs)

        titleEditText?.text?.clear()
        artistEditText?.text?.clear()
        albumEditText?.text?.clear()
        checkBoxFavorite?.isChecked = false

        songAdapter.updateSongs(allSongs)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ALL_SONGS_REQUEST_CODE && resultCode == RESULT_OK) {
            val bundle = data?.extras
            allSongs = bundle?.getSerializable("allSongs") as? MutableList<Song> ?: mutableListOf()
            albums = bundle?.getSerializable("albums") as? MutableList<Album> ?: mutableListOf()
            artists = bundle?.getSerializable("artists") as? MutableList<Artist> ?: mutableListOf()
            favorites = bundle?.getSerializable("favorites") as? MutableList<Song> ?: mutableListOf()

            songAdapter.updateSongs(allSongs)
        }
    }

    private inner class SongAdapter(private val context: AppCompatActivity, private var songs: MutableList<Song>) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

        inner class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
            val artistTextView: TextView = itemView.findViewById(R.id.textViewArtist)
            val albumTextView: TextView = itemView.findViewById(R.id.textViewAlbum)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false)
            return SongViewHolder(view)
        }

        override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
            val song = songs[position]
            holder.titleTextView.text = song.title
            holder.artistTextView.text = song.artist
            holder.albumTextView.text = song.album

            if (song.isFavorite) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow)) // Замените R.color.yellow на ваш цвет
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)}

            holder.itemView.setOnClickListener {
                showEditSongDialog(song, position)
            }
        }




        private fun showEditSongDialog(song: Song, position: Int) {
            val builder = AlertDialog.Builder(context)
            val inflater = context.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_add_song, null)
            val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
            val editTextArtist = dialogView.findViewById<EditText>(R.id.editTextArtist)
            val editTextAlbum = dialogView.findViewById<EditText>(R.id.editTextAlbum)
            val checkBoxFavorite = dialogView.findViewById<CheckBox>(R.id.checkBoxFavorite)

            editTextTitle.setText(song.title)
            editTextArtist.setText(song.artist)
            editTextAlbum.setText(song.album)
            checkBoxFavorite.isChecked = song.isFavorite

            builder.setView(dialogView)
                .setPositiveButton("Изменить") { _: DialogInterface, _: Int ->
                    val title = editTextTitle.text.toString().trim()
                    val artist = editTextArtist.text.toString().trim()
                    val album = editTextAlbum.text.toString().trim()
                    val isFavorite = checkBoxFavorite.isChecked

                    if (title.isEmpty() || artist.isEmpty() || album.isEmpty()) {
                        Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }//Update song and refresh list
                    val updatedSong = Song(title, artist, album, isFavorite)
                    songs[position] = updatedSong

                    if (isFavorite && !favorites.contains(updatedSong)) {
                        favorites.add(updatedSong)
                    } else if (!isFavorite && favorites.contains(song)) {
                        favorites.remove(song)
                    }
                    notifyItemChanged(position)

                }
                .setNegativeButton("Отмена", null) // Null listener dismisses automatically
                .show()
        }



        override fun getItemCount(): Int {
            return songs.size
        }

        fun updateSongs(newSongs: MutableList<Song>) {
            songs = newSongs
            notifyDataSetChanged()
        }

    }
}
