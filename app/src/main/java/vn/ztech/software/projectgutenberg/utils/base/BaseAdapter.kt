package vn.ztech.software.projectgutenberg.utils.base

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import vn.ztech.software.projectgutenberg.utils.Constant

abstract class BaseAdapter<VH : ViewHolder> : RecyclerView.Adapter<VH>() {
    var page = Constant.FIRST_PAGE
    fun loadMore(recyclerView: RecyclerView?, handle: (Int) -> Unit) {
        recyclerView?.apply {
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                        val sizeData = recyclerView.adapter?.itemCount?.minus(1)
                        if (
                            linearLayoutManager != null &&
                            isAtLastPosition(linearLayoutManager, sizeData) &&
                            haveNextPage()
                        ) {
                            handle(++page)
                        }
                    }
                }
            )
        }
    }

    private fun isAtLastPosition(layoutManager: LinearLayoutManager, sizeData: Int?): Boolean {
        return layoutManager.findLastCompletelyVisibleItemPosition() == sizeData
                && sizeData != -1
    }

    open fun haveNextPage(): Boolean = false
}
