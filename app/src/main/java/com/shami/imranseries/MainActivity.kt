package com.shami.imranseries

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_books)
        progressBar = findViewById(R.id.progress_bar)
        swipeRefresh = findViewById(R.id.swipe_refresh)
        emptyText = findViewById(R.id.text_empty)

        recyclerView.layoutManager = LinearLayoutManager(this)

        swipeRefresh.setOnRefreshListener { loadBooks() }
        loadBooks()
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
                    0 -> ReaderActivity.start(this, book.title, book.readUrl)
                    1 -> downloadPdf(book)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun downloadPdf(book: Book) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(book.downloadUrl))
            startActivity(intent)
        } catch (e: Exception) {
            // ignore
        }
    }
}
