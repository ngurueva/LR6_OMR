package com.example.lr6_omr

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView

class AllSongsActivity : AppCompatActivity() {
    private lateinit var allSongsAdapter: ArrayAdapter<Song>
    private lateinit var listViewAllSongs: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_all_songs)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        listViewAllSongs = findViewById(R.id.listViewAllSongs)

        val allSongsBundle = intent.getBundleExtra("allSongsBundle")
        val allSongs = allSongsBundle?.getSerializable("allSongs") as? List<Song> ?: listOf()

        allSongsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, allSongs as MutableList<Song>)
        listViewAllSongs.adapter = allSongsAdapter
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

