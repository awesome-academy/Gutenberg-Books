package vn.ztech.software.projectgutenberg.data.repository.source

import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener

interface BookDataSource {

    interface Local {
        fun getBooksLocal(listener: OnResultListener<List<Book>>)
    }

    interface Remote {
        fun getBooks(listener: OnResultListener<List<Book>>)
    }
}
