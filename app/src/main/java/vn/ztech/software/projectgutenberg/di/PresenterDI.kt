package vn.ztech.software.projectgutenberg.di

import android.content.Context
import vn.ztech.software.projectgutenberg.screen.download.ListBookLocalPresenter
import vn.ztech.software.projectgutenberg.screen.home.ListBookPresenter
import vn.ztech.software.projectgutenberg.screen.readbook.ReadBookPresenter

fun getListBookPresenter(context: Context?): ListBookPresenter {
    return ListBookPresenter(
        getBookRepository(context)
    )
}

fun getListLocalBookPresenter(context: Context?): ListBookLocalPresenter {
    return ListBookLocalPresenter(
        getBookRepository(context)
    )
}

fun getReadBookPresenter(context: Context?): ReadBookPresenter {
    return ReadBookPresenter(
        getBookRepository(context)
    )
}

