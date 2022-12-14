package vn.ztech.software.projectgutenberg.screen.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.databinding.ItemBookshelfBinding

class ListBookshelvesAdapter(private val listener: (Bookshelf) -> Unit) :
    RecyclerView.Adapter<ListBookshelvesAdapter.BookViewHolder>() {
    private var bookshelves = mutableListOf<Bookshelf>()

    class BookViewHolder(
        private val binding: ItemBookshelfBinding,
        private val listener: (Bookshelf) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookshelf: Bookshelf) {
            binding.apply {
                tvBookshelf.text = bookshelf.name
                root.setOnClickListener { listener(bookshelf) }
            }
        }
    }

    fun setData(bookshelves: List<Bookshelf>, action: AdapterDataAction = AdapterDataAction.ADD) {
        when (action) {
            AdapterDataAction.ADD -> this.bookshelves.addAll(bookshelves)
            AdapterDataAction.REPLACE -> {
                this.bookshelves.clear()
                this.bookshelves.addAll(bookshelves)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookshelfBinding.inflate(LayoutInflater.from(parent.context))
        return BookViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(bookshelves[position])
    }

    override fun getItemCount(): Int {
        return bookshelves.size
    }

    enum class AdapterDataAction {
        ADD, REPLACE
    }
}
