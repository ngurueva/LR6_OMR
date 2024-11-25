package com.example.lr6_omr

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ArtistsActivity : AppCompatActivity() {
    private lateinit var artistsAdapter: ArrayAdapter<Song>
    private lateinit var listViewArtists: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_artists)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        listViewArtists = findViewById(R.id.listViewArtists)

        val artistsBundle = intent.getBundleExtra("artistsBundle")
        val artists = artistsBundle?.getSerializable("artists") as? List<Song> ?: listOf()

        artistsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, artists as MutableList<Song>)
        listViewArtists.adapter = artistsAdapter
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