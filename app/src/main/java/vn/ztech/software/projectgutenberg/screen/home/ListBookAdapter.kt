package vn.ztech.software.projectgutenberg.screen.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.databinding.ItemBookBinding
import vn.ztech.software.projectgutenberg.utils.extension.findCoverImageURL
import vn.ztech.software.projectgutenberg.utils.extension.findShowableAgent

class ListBookAdapter(private val listener: OnClickListener) :
    RecyclerView.Adapter<ListBookAdapter.BookViewHolder>() {
    private var books = mutableListOf<Book>()

    interface OnClickListener {
        fun onItemClick(book: Book)
    }

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val listener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            binding.apply {
                book.agents.findShowableAgent()?.let {
                    tvAuthor.text = it.person
                }
                tvTitle.text = book.title
                book.resources.findCoverImageURL()?.apply {
                    Glide.with(binding.root).load(this).centerCrop().into(ivBookCover)
                }
                root.setOnClickListener { listener.onItemClick(book) }
            }
        }
    }

    fun setData(books: List<Book>, action: AdapterDataAction = AdapterDataAction.ADD) {
        when (action) {
            AdapterDataAction.ADD -> this.books.addAll(books)
            AdapterDataAction.REPLACE -> {
                this.books.clear()
                this.books.addAll(books)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context))
        return BookViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int {
        return books.size
    }

    enum class AdapterDataAction {
        ADD, REPLACE
    }
}
