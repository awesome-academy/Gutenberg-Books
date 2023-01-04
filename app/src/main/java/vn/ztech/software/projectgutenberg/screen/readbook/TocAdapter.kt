package vn.ztech.software.projectgutenberg.screen.readbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.databinding.ItemTocBinding

class TocAdapter(val toc: Toc = Toc(), val listener: OnClickListener) :
    RecyclerView.Adapter<TocAdapter.TocItemViewHolder>() {
    private val tocItems
        get() = toc.listItem
    var currentSelectedItem = tocItems.firstOrNull()

    inner class TocItemViewHolder(val binding: ItemTocBinding, val listener: OnClickListener) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tocItem: TocItem) {
            binding.tvTitle.text = tocItem.title
            binding.progressBarReading.progress = tocItem.progressStringToPercentage()
            if (tocItem.isDone()) {
                binding.tvPercent.visibility = View.GONE
                binding.progressBarReading.visibility = View.INVISIBLE
                binding.ivDoneChecked.visibility = View.VISIBLE

            } else {
                binding.tvPercent.visibility = View.VISIBLE
                binding.ivDoneChecked.visibility = View.GONE
                binding.tvPercent.text = tocItem.getProgressToDisplay()
                binding.progressBarReading.visibility = View.VISIBLE

            }
            currentSelectedItem?.let { binding.root.isSelected = tocItem.idx == it.idx }

            binding.root.setOnClickListener {
                listener.onTocItemClick(tocItem)
            }
        }
    }

    fun setData(list: List<TocItem>) {
        toc.listItem.clear()
        toc.listItem.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TocItemViewHolder {
        val binding = ItemTocBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TocItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: TocItemViewHolder, position: Int) {
        holder.bind(tocItems[position])
    }

    override fun getItemCount(): Int {
        return tocItems.size
    }

    fun updateTocItem(tocItem: TocItem) {
        val idx = tocItems.indexOfFirst { it.bookId == tocItem.bookId && it.href == tocItem.href }
        tocItems[idx] = tocItem
        notifyItemChanged(idx)
    }

    fun updateCurrentSelectedItem(tocItem: TocItem) {
        currentSelectedItem = tocItem
        notifyDataSetChanged()
    }

    interface OnClickListener {
        fun onTocItemClick(tocItem: TocItem)
    }
}
