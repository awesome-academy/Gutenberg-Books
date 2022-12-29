package vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading

import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem

interface BookReadingDbDAOReadableInterface {

    fun getToc(id: Int): Toc

}

interface BookReadingDbDAOWritableInterface {
    fun insertOrIgnoreTocItem(
        book: BookLocal,
        tocItem: TocItem,
    ): Boolean

    fun storeToc(book: BookLocal, epubFile: EpubFile): Boolean

}
