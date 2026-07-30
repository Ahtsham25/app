package com.shami.imranseries

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPreviousCrash()

        recyclerView = findViewById(R.id.recycler_books)
        progressBar = findViewById(R.id.progress_bar)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        emptyText = findViewById(R.id.text_empty)

        recyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefresh.setOnRefreshListener { loadBooks() }
        loadBooks()
    }

    private fun checkPreviousCrash() {
        val file = File(filesDir, "crash.txt")
        if (file.exists()) {
            val content = file.readText()
            AlertDialog.Builder(this)
                .setTitle("Pichli crash ka error")
                .setMessage(content)
                .setPositiveButton("Copy") { _, _ ->
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("crash", content))
                    Toast.makeText(this, "Copy ho gaya", Toast.LENGTH_SHORT).show()
                    file.delete()
                }
                .setNegativeButton("Band karein") { _, _ -> file.delete() }
                .show()
        }
    }

    private fun loadBooks() {
        progressBar.visibility = ProgressBar.VISIBLE
        emptyText.visibility = TextView.GONE
        thread {
            val books = try {
                BooksRepository.loadBooks(this)
            } catch (e: Exception) {
                emptyList()
            }
            runOnUiThread {
                progressBar.visibility = ProgressBar.GONE
                swipeRefresh.isRefreshing = false
                if (books.isEmpty()) {
                    emptyText.visibility = TextView.VISIBLE
                    emptyText.text = getString(R.string.load_failed)
                } else {
                    recyclerView.adapter = BookAdapter(books) { book -> showBookOptions(book) }
                }
            }
        }
    }

    private fun showBookOptions(book: Book) {
        AlertDialog.Builder(this)
            .setTitle(book.title)
            .setItems(arrayOf(getString(R.string.read_online), getString(R.string.download))) { _, which ->
                when (which) {
                    0 -> ReaderActivity.start(this, book.title, book.embedUrl)
                    1 -> downloadPdf(book)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun downloadPdf(book: Book) {
        try {
            val fileName = "${book.number} - ${book.title}.pdf"
            val request = DownloadManager.Request(Uri.parse(book.downloadUrl))
                .setTitle(book.title)
                .setDescription(getString(R.string.downloading))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "ImranSeries/$fileName"
                )
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

            val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(this, getString(R.string.download_started, fileName), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Download shuru nahi ho saka", Toast.LENGTH_SHORT).show()
        }
    }
}
