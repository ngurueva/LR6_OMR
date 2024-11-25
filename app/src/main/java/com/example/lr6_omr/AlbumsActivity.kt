package com.example.lr6_omr

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AlbumsActivity : AppCompatActivity() {
    private lateinit var albumsAdapter: ArrayAdapter<Song>
    private lateinit var listViewAlbums: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_albums)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        listViewAlbums = findViewById(R.id.listViewAlbums)

        val albumsBundle = intent.getBundleExtra("albumsBundle")
        val albums = albumsBundle?.getSerializable("albums") as? List<Song> ?: listOf()

        albumsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, albums as MutableList<Song>)
        listViewAlbums.adapter = albumsAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}