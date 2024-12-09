package com.example.lr6_omr


import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Serializable


import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import androidx.core.content.FileProvider


class MainActivity : AppCompatActivity() {
    var allSongs = mutableListOf<Song>()
    var favorites = mutableListOf<Song>()
    var albums = mutableListOf<Album>()
    var artists = mutableListOf<Artist>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        recyclerView = findViewById(R.id.recyclerViewMain) // Find the view AFTER setContentView
        recyclerView.layoutManager = LinearLayoutManager(this)


        val songStorage = SongStorage(this)
        allSongs = songStorage.loadSongs()
        songAdapter = SongAdapter(this, allSongs)
        recyclerView.adapter = songAdapter

        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        buttonAdd.setOnClickListener { addSong() }

        val buttonSaveCSV = findViewById<Button>(R.id.btnSaveCSV)
        buttonSaveCSV.setOnClickListener { saveDataToCSV(this) }

        val buttonOpenCSV = findViewById<Button>(R.id.btnOpenCSV)
        buttonOpenCSV.setOnClickListener { readDataFromCSV(this) }

        val buttonSavePDF = findViewById<Button>(R.id.btnSavePDF)
        buttonSavePDF.setOnClickListener { saveToPDF(this) }

        val buttonOpenPDF = findViewById<Button>(R.id.btnOpenPDF)
        buttonOpenPDF.setOnClickListener { openPdfFile(this, "data") }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


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

    override fun onPause() {
        super.onPause()
        saveSongsToStorage()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveSongsToStorage()
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
//        saveToPDF(this)
    }

    fun saveDataToCSV(context: Context) {
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = "Введите имя файла"

        builder.setView(input)
            .setTitle("Сохранение в CSV")
            .setPositiveButton("Сохранить") { _, _ ->
                val fileName = input.text.toString().trim()
                if (fileName.isEmpty()) {
                    Toast.makeText(context, "Введите имя файла", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Для Android 10+
                    saveToMediaStore(context, fileName)
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }


    private fun saveToMediaStore(context: Context, fileName: String) {
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.csv")
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
        }

        try {
            val resolver = context.contentResolver
            val uri: Uri? = resolver.insert(MediaStore.Files.getContentUri("external"), values)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    saveToOutputStream(outputStream, fileName)
                }
            } ?: run {
                Toast.makeText(context, "Ошибка при создании файла", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Ошибка при сохранении файла в MediaStore", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveToOutputStream(outputStream: OutputStream, fileName: String) {
        BufferedWriter(OutputStreamWriter(outputStream, Charsets.UTF_8)).use { writer ->
            writer.write("№;name;author;album;isFavorite\n")
            for ((index, song) in allSongs.withIndex()) {
                writer.write("${index + 1};${song.title};${song.artist};${song.album};${song.isFavorite}\n")
            }
        }
    }

    fun readDataFromCSV(context: Context): List<Song> {
        val allSongs = mutableListOf<Song>()
        val builder = AlertDialog.Builder(context)
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = "Введите имя файла (без расширения .csv)"

        builder.setView(input)
            .setTitle("Чтение из CSV")
            .setPositiveButton("Читать") { _, _ ->
                val fileName = input.text.toString().trim()
                if (fileName.isEmpty()) {
                    Toast.makeText(context, "Введите имя файла", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val songs = readCSV(context, fileName)
                allSongs.addAll(songs)
                songAdapter.updateSongs(allSongs)
            }
            .setNegativeButton("Отмена", null)
            .show()
        return allSongs
    }


    private fun readCSV(context: Context, fileName: String): List<Song> {
        val songList = mutableListOf<Song>()
        val resolver = context.contentResolver
        val uri = MediaStore.Files.getContentUri("external")
        val selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?"
        val selectionArgs = arrayOf("$fileName.csv")
        val projection = arrayOf(MediaStore.MediaColumns.DATA)

        try {
            resolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                    File(filePath).useLines { lines ->
                        lines.drop(1).forEach { line ->
                            val parts = line.split(';')
                            if (parts.size == 5) {
                                try {
                                    val song = Song(parts[1], parts[2], parts[3], parts[4].toBoolean())
                                    songList.add(song)
                                    Log.d("Song", song.toString())
                                } catch (e: Exception) {
                                    Log.e("readDataFromCSV", "Error parsing line: $line", e)
                                }
                            } else {
                                Log.w("readDataFromCSV", "Incorrect number of elements in line: $line")
                            }
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("readDataFromCSV", "SecurityException: Insufficient permissions", e)
            Toast.makeText(context, "Недостаточно прав для доступа к файлу", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            Log.e("readDataFromCSV", "FileNotFoundException: File not found", e)
            Toast.makeText(context, "Файл не найден", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("readDataFromCSV", "IOException: Error reading file", e)
            Toast.makeText(context, "Ошибка чтения файла", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("readDataFromCSV", "Unexpected error", e)
            Toast.makeText(context, "Произошла неизвестная ошибка", Toast.LENGTH_SHORT).show()
        }
        return songList
    }




    fun saveToPDF(context: Context) {
        try {
            savePdfToInternalStorage(context, "data")
        } catch (e: IOException) {
            Toast.makeText(context, "Ошибка при сохранении PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    @Throws(IOException::class)
    private fun savePdfToInternalStorage(context: Context, fileName: String) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        paint.setTypeface(Typeface.DEFAULT)
        paint.setTextSize(12f)

        val pageInfo = PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        var y = 50f
        for ((index, song) in allSongs.withIndex()) {
            canvas.drawText("Название: ${song.title}", 50f, y, paint)
            y += 20f
            canvas.drawText("Исполнитель: ${song.artist}", 50f, y, paint)
            y += 20f
            canvas.drawText("Альбом: ${song.album}", 50f, y, paint)
            y += 20f
            canvas.drawText("Избранное: ${song.isFavorite}", 50f, y, paint)
            y += 40f
        }
        pdfDocument.finishPage(page)

        val file = File(context.filesDir, "$fileName.pdf")
        if (file.exists()) file.delete()
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()

        Toast.makeText(context, "PDF сохранен в ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }




    private fun openPdfFile(context: Context, fileName: String) {
        val file = File(context.filesDir, "$fileName.pdf")
        if (!file.exists()) {
            Toast.makeText(context, "Файл не найден", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file) //Requires adding FileProvider to manifest

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //Needed for starting activity from service

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Нет приложения для открытия PDF", Toast.LENGTH_SHORT).show()
        }
    }



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
        saveSongsToStorage()
    }
    private fun saveSongsToStorage() {
        val songStorage = SongStorage(this)
        songStorage.saveSongs(allSongs)
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
