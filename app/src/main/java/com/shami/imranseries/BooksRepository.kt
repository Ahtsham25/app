package com.shami.imranseries

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object BooksRepository {

    // TODO: apna GitHub raw books.json link yahan daalein, e.g.
    // "https://raw.githubusercontent.com/USERNAME/REPO/main/books.json"
    private const val REMOTE_BOOKS_URL = "https://raw.githubusercontent.com/YOUR_USERNAME/YOUR_REPO/main/books.json"

    fun loadBooks(context: Context): List<Book> {
        val json = fetchRemote() ?: fetchLocalAsset(context)
        return parseBooks(json)
    }

    private fun fetchRemote(): String? {
        return try {
            val conn = URL(REMOTE_BOOKS_URL).openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.requestMethod = "GET"
            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val text = reader.readText()
                reader.close()
                text
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun fetchLocalAsset(context: Context): String {
        val input = context.assets.open("books.json")
        val reader = BufferedReader(InputStreamReader(input))
        val text = reader.readText()
        reader.close()
        return text
    }

    private fun parseBooks(json: String): List<Book> {
        val root = JSONObject(json)
        val arr = root.getJSONArray("books")
        val list = mutableListOf<Book>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                Book(
                    id = o.getString("id"),
                    title = o.getString("title"),
                    number = o.getString("number"),
                    embedUrl = o.getString("embed_url"),
                    downloadUrl = o.getString("download_url"),
                    detailsUrl = o.optString("details_url", "")
                )
            )
        }
        return list
    }
}

