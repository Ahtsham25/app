package com.shami.imranseries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(
    private val books: List<Book>,
    private val onClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.card_book)
        val number: TextView = view.findViewById(R.id.text_number)
        val title: TextView = view.findViewById(R.id.text_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.number.text = book.number
        holder.title.text = book.title
        holder.card.setOnClickListener { onClick(book) }
    }

    override fun getItemCount(): Int = books.size
}
