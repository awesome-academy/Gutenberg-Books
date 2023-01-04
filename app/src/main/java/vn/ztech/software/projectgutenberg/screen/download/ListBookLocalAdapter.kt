package vn.ztech.software.projectgutenberg.screen.download

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.BaseDataLocal
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.databinding.ItemDownloadedBookBinding
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BaseAdapterLocal
import vn.ztech.software.projectgutenberg.utils.extension.equalsById
import vn.ztech.software.projectgutenberg.utils.extension.getLastPart
import vn.ztech.software.projectgutenberg.utils.extension.loadImage
import vn.ztech.software.projectgutenberg.utils.extension.removeUnderScore
import vn.ztech.software.projectgutenberg.utils.extension.toReadableFileSize

class ListBookLocalAdapter(private val listener: OnClickListener) :
    BaseAdapterLocal<BookLocal, ListBookLocalAdapter.BookViewHolder>(BookLocalDifferCallback()) {
    private var data = BaseDataLocal<BookLocal>()

    interface OnClickListener {
        fun onItemClick(book: BookLocal)
        fun onDeleteButtonClicked(book: BookLocal)
    }

    class BookViewHolder(
        private val binding: ItemDownloadedBookBinding,
        private val listener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: BookLocal) {
            binding.apply {
                tvTitle.text = book.title.removeUnderScore()
                tvAuthor.text = book.mimeType.getLastPart()
                if (book.imageUrl.isNotEmpty() && book.imageUrl != Constant.STRING_NULL)
                    ivBookCover.loadImage(book.imageUrl)
                else
                    ivBookCover.loadImage(R.drawable.splash)

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

                root.setOnClickListener { listener.onItemClick(book) }
                btDelete.setOnClickListener { listener.onDeleteButtonClicked(book) }
            }
        }
    }

    fun setData(data: BaseDataLocal<BookLocal>, action: DataAction) {
        addList(data.results, action)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding =
            ItemDownloadedBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setLoading(book: BookLocal, isLoading: Boolean = true) {
        val newBook = book.copy(isProcessing = isLoading)
        addOne(newBook, action = DataAction.REPLACE_ONE) { oldBook, book ->
            oldBook.equalsById(book)
        }
    }


    private class BookLocalDifferCallback : DiffUtil.ItemCallback<BookLocal>() {
        override fun areItemsTheSame(oldItem: BookLocal, newItem: BookLocal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BookLocal, newItem: BookLocal): Boolean {
            return oldItem == newItem && oldItem.getReadingPercentage() == newItem.getReadingPercentage()
                    && oldItem.imageUrl == newItem.imageUrl
        }
    }
}
