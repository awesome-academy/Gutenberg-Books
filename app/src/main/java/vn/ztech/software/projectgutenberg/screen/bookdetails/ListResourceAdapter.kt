package vn.ztech.software.projectgutenberg.screen.bookdetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.databinding.ItemResourceDownloadBinding
import vn.ztech.software.projectgutenberg.databinding.ItemResourcePreviewBinding
import vn.ztech.software.projectgutenberg.utils.Constant

class ListResourceAdapter(private val listener: OnClickListener) :
    RecyclerView.Adapter<ViewHolder>() {
    private var resources = mutableListOf<Resource>()

    interface OnClickListener {
        fun onItemClick(resource: Resource)
    }

    class ResourcePreviewViewHolder(
        private val binding: ItemResourcePreviewBinding,
        private val listener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(resource: Resource) {
            binding.apply {
                tvResourceTitle.text = resource.actionType.name
                tvResourceUri.text = "${resource.kindShort}   "
                root.setOnClickListener { listener.onItemClick(resource) }
            }
        }
    }

    class ResourceDownloadViewHolder(
        private val binding: ItemResourceDownloadBinding,
        private val listener: OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(resource: Resource) {
            binding.apply {
                tvResourceTitle.text = resource.actionType.name
                tvResourceUri.text = "${resource.kindShort}   "
                root.setOnClickListener { listener.onItemClick(resource) }
            }
        }
    }

    fun setData(resources: List<Resource>, action: AdapterDataAction = AdapterDataAction.ADD) {
        when (action) {
            AdapterDataAction.ADD -> this.resources.addAll(resources)
            AdapterDataAction.REPLACE -> {
                this.resources.clear()
                this.resources.addAll(resources)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            Constant.ActionTypes.DOWNLOAD.ordinal -> {
                ResourceDownloadViewHolder(
                    ItemResourceDownloadBinding.inflate(LayoutInflater.from(parent.context)),
                    listener
                )
            }
            Constant.ActionTypes.PREVIEW.ordinal -> {
                ResourcePreviewViewHolder(
                    ItemResourcePreviewBinding.inflate(LayoutInflater.from(parent.context)),
                    listener
                )
            }
            else -> {
                ResourcePreviewViewHolder(
                    ItemResourcePreviewBinding.inflate(LayoutInflater.from(parent.context)),
                    listener
                )
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Constant.ActionTypes.PREVIEW.ordinal -> {
                (holder as ResourcePreviewViewHolder).bind(resources[position])
            }
            Constant.ActionTypes.DOWNLOAD.ordinal -> {
                (holder as ResourceDownloadViewHolder).bind(resources[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return resources.size
    }

    override fun getItemViewType(position: Int): Int {
        return resources[position].actionType.ordinal
    }

    enum class AdapterDataAction {
        ADD, REPLACE
    }
}
