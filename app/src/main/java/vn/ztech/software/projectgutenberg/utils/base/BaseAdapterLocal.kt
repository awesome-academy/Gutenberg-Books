package vn.ztech.software.projectgutenberg.utils.base

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.ztech.software.projectgutenberg.utils.Constant
import java.util.concurrent.Executors

abstract class BaseAdapterLocal<M, VH : ViewHolder>(diffCallback: DiffUtil.ItemCallback<M>) :
    ListAdapter<M, VH>(
        AsyncDifferConfig.Builder<M>(diffCallback)
            .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor())
            .build()
    ) {
    var handle: (Int) -> Unit = {}
    var updateNewData: (Int) -> Unit = {}
    var loadMoreEnable = true
    fun addList(list: List<M>, action: DataAction) {
        val set = mutableSetOf<M>()
        when (action) {
            DataAction.ADD -> {
                set.addAll(currentList.toMutableSet())
                set.addAll(list)
                offset = set.size
            }
            DataAction.REPLACE -> {
                set.addAll(list)
                offset = set.size
                submitList(set.toList())
            }
            else -> {
                // todo implement later
            }
        }
        submitList(set.toList())
    }

    fun addOne(item: M, action: DataAction, comparator: ((M, M) -> Boolean)? = null) {
        val set = mutableSetOf<M>()
        when (action) {
            DataAction.REPLACE_ONE -> {
                comparator?.let {
                    val indexOfChangedItem = currentList.indexOfFirst { comparator(it, item) }
                    set.addAll(currentList.subList(0, indexOfChangedItem).toList())
                    set.add(item)
                    set.addAll(
                        currentList.subList(indexOfChangedItem + 1, currentList.size).toList()
                    )
                    offset = set.size
                    submitList(set.toList())
                }

            }
            else -> {
                //todo implement later
            }
        }
        submitList(set.toList())
    }


    var offset = Constant.LOCAL_DATA_QUERY_PAGE_SIZE
    var isLoading = false

    fun loadMore(recyclerView: RecyclerView?, handle: (Int) -> Unit) {
        recyclerView?.apply {
            this@BaseAdapterLocal.handle = handle
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val sizeData = recyclerView.adapter?.itemCount?.minus(1)
                        if (
                            linearLayoutManager != null &&
                            isAtLastPosition(linearLayoutManager, sizeData) &&
                            isCanLoad(isLoading, loadMoreEnable)
                        ) {
                            isLoading = true
                            handle(offset)
                        }
                    }
                }
            )
        }
    }

    fun mSetUpdateNewData(handleUpdate: (Int) -> Unit) {
        updateNewData = handleUpdate
    }

    fun isAtLastPosition(layoutManager: LinearLayoutManager, sizeData: Int?): Boolean {
        return layoutManager.findLastCompletelyVisibleItemPosition() == sizeData
                && sizeData != -1
    }

    fun isCanLoad(isLoading: Boolean, loadMoreEnable: Boolean): Boolean {
        return !isLoading && loadMoreEnable
    }

    fun setLastItem(offset: Int) {
        this.offset = offset
    }

    fun loadDone() {
        isLoading = false
    }

    fun updateNewDataAndLoadMore() {
        updateNewData(offset)
        /**reload previous page*/
    }

    fun setLoadMore(enable: Boolean) {
        this.loadMoreEnable = enable
    }

    enum class DataAction {
        ADD, REPLACE, REPLACE_ONE
    }
}
