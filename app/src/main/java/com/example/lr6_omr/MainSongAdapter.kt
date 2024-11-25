package com.example.lr6_omr

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainSongAdapter(private val songs: MutableList<Song>) : RecyclerView.Adapter<MainSongAdapter.ViewHolder>() {    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.songTitle)
        val artistTextView: TextView = itemView.findViewById(R.id.songArtist)
        val albumTextView: TextView = itemView.findViewById(R.id.songAlbum)
        val favoriteCheckBox: CheckBox = itemView.findViewById(R.id.checkBoxFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_song, parent, false) //Replace song_item_layout with your item layout file
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.titleTextView.text = song.title
        holder.artistTextView.text = song.artist
        holder.albumTextView.text = song.album
        holder.favoriteCheckBox.isChecked = song.isFavorite
//        holder.itemView.setOnClickListener { onSongClickListener(song) }
    }

    override fun getItemCount(): Int = songs.size

    fun updateData(newSongs: MutableList<Song>){
        songs.clear()
        songs.addAll(newSongs)
        notifyDataSetChanged()
    }
}

