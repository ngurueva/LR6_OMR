package com.example.lr6_omr

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException

class PdfViewerActivity : AppCompatActivity() {

    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer) // Создайте layout с ImageView

        imageView = findViewById(R.id.imageView) // Замените R.id.imageView на ID вашего ImageView

        val file = File(filesDir, "data.pdf") // Путь к вашему PDF-файлу
        if (file.exists()) {
            try {
                pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                showPage(0) // Показать первую страницу
            } catch (e: IOException) {
                Toast.makeText(this, "Ошибка при открытии PDF: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        } else {
            Toast.makeText(this, "PDF-файл не найден!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun showPage(pageNumber: Int) {
        if (pageNumber < 0 || pageNumber >= pdfRenderer?.pageCount ?: 0) return

        currentPage?.close()
        currentPage = pdfRenderer?.openPage(pageNumber)
        val bitmap = Bitmap.createBitmap(currentPage?.width ?: 0, currentPage?.height ?: 0, Bitmap.Config.ARGB_8888)
        currentPage?.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        imageView.setImageBitmap(bitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPage?.close()
        pdfRenderer?.close()
    }
}
