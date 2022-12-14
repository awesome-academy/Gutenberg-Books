package vn.ztech.software.projectgutenberg.di

import android.content.Context
import vn.ztech.software.projectgutenberg.screen.home.ListBookPresenter

fun getListBookPresenter(context: Context?): ListBookPresenter {
    return ListBookPresenter(
        getBookRepository(context)
    )
}
