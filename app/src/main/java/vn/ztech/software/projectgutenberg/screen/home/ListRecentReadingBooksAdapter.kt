package vn.ztech.software.projectgutenberg.screen.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.databinding.ItemDownloadedBookBinding
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.extension.getLastPart
import vn.ztech.software.projectgutenberg.utils.extension.loadImage
import vn.ztech.software.projectgutenberg.utils.extension.removeUnderScore
import vn.ztech.software.projectgutenberg.utils.extension.toReadableFileSize

class ListRecentReadingBooksAdapter(private val listener: (BookLocal) -> Unit) :
    RecyclerView.Adapter<ListRecentReadingBooksAdapter.RecentReadingBookViewHolder>() {
    private var books = mutableListOf<BookLocal>()

    class RecentReadingBookViewHolder(
        private val binding: ItemDownloadedBookBinding,
        private val listener: (BookLocal) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: BookLocal) {
            binding.apply {
                tvTitle.text = book.title.removeUnderScore()
                tvAuthor.text = book.mimeType.getLastPart()
                if (book.imageUrl.isNotEmpty() && book.imageUrl != Constant.STRING_NULL)
                    ivBookCover.loadImage(book.imageUrl)
                tvSize.text = book.size.toReadableFileSize()

                if (book.prepared == BookLocal.PREPARED) {
                    groupReadingProgress.visibility = View.VISIBLE
                    tvPrepareToRead.visibility = View.GONE
                    book.getReadingPercentage()?.let {
                        progressBar.progress = it
                        tvRecentReadingPercentage.text = it.toString()
                    }
                } else {
                    groupReadingProgress.visibility = View.GONE
                    tvPrepareToRead.visibility = View.VISIBLE
                }

                if (book.isProcessing) {
                    layoutLoading.layoutLoading.visibility = View.VISIBLE
                } else {
                    layoutLoading.layoutLoading.visibility = View.GONE
                }
                btDelete.visibility = View.GONE
                root.setOnClickListener { listener(book) }
            }
        }
    }

    fun setData(books: List<BookLocal>) {
        this.books.clear()
        this.books.addAll(books)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentReadingBookViewHolder {
        val binding =
            ItemDownloadedBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentReadingBookViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecentReadingBookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int {
        return books.size
    }
}
