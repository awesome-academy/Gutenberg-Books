package vn.ztech.software.projectgutenberg.screen.home

import androidx.core.widget.NestedScrollView

abstract class NestedRecyclerViewPaginator : NestedScrollView.OnScrollChangeListener {
    private var page = 1
    private var isLoading = false
    override fun onScrollChange(
        v: NestedScrollView,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (scrollY > ((v.getChildAt(0).measuredHeight - v.measuredHeight) - 2 * v.measuredHeight)) {
            /** When scroll over 80% of the height of nested scroll view */
            if (!isLoading) {
                isLoading = true
                onHitBottom(++page)
            }
        }
    }

    abstract fun onHitBottom(page: Int)
    fun onLoadDone() {
        isLoading = false
    }
}
