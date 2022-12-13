package vn.ztech.software.projectgutenberg.data.repository.source.local

import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.BookDataSource

class BookLocalDataSource : BookDataSource.Local {
    override fun getBooksLocal(listener: OnResultListener<List<Book>>) {
        //TODO Implement later
    }

    companion object {
        private var instance: BookLocalDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: BookLocalDataSource().also { instance = it }
        }
    }
}
