package com.example.lr6_omr

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FavoritesActivity : AppCompatActivity() {
    private lateinit var favoritesAdapter: ArrayAdapter<Song>
    private lateinit var listViewFavorites: ListView // Declare the ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        listViewFavorites = findViewById(R.id.listViewFavorites)

        val favoritesBundle = intent.getBundleExtra("favoritesBundle")
        val favorites = favoritesBundle?.getSerializable("favorites") as? List<Song> ?: listOf()

        favoritesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, favorites as MutableList<Song>)
        listViewFavorites.adapter = favoritesAdapter
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

