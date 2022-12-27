package vn.ztech.software.projectgutenberg.screen.download

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import vn.ztech.software.projectgutenberg.data.model.BaseDataLocal
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.databinding.ItemDownloadedBookBinding
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BaseAdapterLocal
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
                tvSize.text = book.size.toReadableFileSize()
                tvRecentReadingPercentage.text = book.readingProgress
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

    private class BookLocalDifferCallback : DiffUtil.ItemCallback<BookLocal>() {
        override fun areItemsTheSame(oldItem: BookLocal, newItem: BookLocal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BookLocal, newItem: BookLocal): Boolean {
            return oldItem == newItem
        }
    }
}
