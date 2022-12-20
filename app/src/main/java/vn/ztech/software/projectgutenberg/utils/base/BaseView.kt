package vn.ztech.software.projectgutenberg.utils.base

import vn.ztech.software.projectgutenberg.utils.Constant

interface BaseView {
    fun updateLoading(loadingArea: Constant.LoadingArea, state: Constant.LoadingState)
}
