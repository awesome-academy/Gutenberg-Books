package vn.ztech.software.projectgutenberg.screen.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.ztech.software.projectgutenberg.data.model.BaseData
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.databinding.ItemBookBinding
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BaseAdapter
import vn.ztech.software.projectgutenberg.utils.extension.findCoverImageURL
import vn.ztech.software.projectgutenberg.utils.extension.findShowableAgent

class ListBookAdapter(private val listener: (Book) -> Unit) :
    BaseAdapter<ListBookAdapter.BookViewHolder>() {
    private var data = BaseData<Book>()
    private val books
        get() = data.results


    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val listener: (Book) -> Unit
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
                root.setOnClickListener { listener(book) }
            }
        }
    }

    fun setData(newData: BaseData<Book>, action: AdapterDataAction = AdapterDataAction.ADD) {
        when (action) {
            AdapterDataAction.ADD -> {
                data.apply {
                    count = newData.count
                    previous = newData.previous
                    next = newData.next
                    results.addAll(newData.results)
                }
            }
            AdapterDataAction.REPLACE -> {
                data.apply {
                    count = newData.count
                    previous = newData.previous
                    next = newData.next
                    results.clear()
                    results.addAll(newData.results)
                }

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

    override fun haveNextPage(): Boolean {
        return data.next != null && data.next != Constant.STRING_NULL
    }

    enum class AdapterDataAction {
        ADD, REPLACE
    }

}
