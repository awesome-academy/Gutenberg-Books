package vn.ztech.software.projectgutenberg

import android.content.Context
import io.mockk.MockK
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository
import vn.ztech.software.projectgutenberg.screen.download.DownloadFragment
import vn.ztech.software.projectgutenberg.screen.download.ListBookLocalContract
import vn.ztech.software.projectgutenberg.screen.download.ListBookLocalPresenter
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter
import vn.ztech.software.projectgutenberg.utils.extension.toBaseDataLocal

class ListBookLocalPresenterTest {
    private val mView = mockk<ListBookLocalContract.View>(relaxed = true)
    private val bookRepository = mockk<BookRepository>()
    private val listBookLocalPresenter = ListBookLocalPresenter(bookRepository)
    private val callbackListBookLocal = slot<OnResultListener<List<BookLocal>>>()
    private val callbackBoolean = slot<OnResultListener<Boolean>>()
    private val callbackString = slot<OnResultListener<String>>()
    private val callbackEpubFile = slot<OnResultListener<EpubFile>>()

    private val context = mockk<Context>(relaxed = true)
    private val book = BookLocal(0, "Fake book", "fake Name", 0L, "", 0L, "")
    private val e = mockk<Exception>()
    val books = listOf<BookLocal>(BookLocal(0, "Fake book", "fake Name", 0L, "", 0L, ""))
    @Test
    fun `getDownloadedBooks onSuccess`(){
        val offset = 0
        val books = listOf<BookLocal>(BookLocal(0, "Fake book", "fake Name", 0L, "", 0L, ""))

        every {
          bookRepository.localReadableObj.getBooksLocal(offset, capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onSuccess(books)
        }

        listBookLocalPresenter.getDownloadedBooks(offset)
        verify {
            mView.onHitLastPage(
                offset
            )
            mView.onGetBooksSuccess(
                books.toBaseDataLocal(enableNextPage = true),
                Constant.LoadingAreaDownloadedBook.DownloadedBookMain,
                DownloadFragment.Companion.GetBooksActionType.LOAD
            )
            mView.updateLoading(Constant.LoadingAreaDownloadedBook.DownloadedBookMain,
                Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `getDownloadedBooks onError`(){
        val e = Exception()
        val offset = 0

        every {
            bookRepository.localReadableObj.getBooksLocal(offset, capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onError(e)
        }

        listBookLocalPresenter.getDownloadedBooks(offset)
        verify {
            mView.updateLoading( Constant.LoadingAreaDownloadedBook.DownloadedBookMain,
                Constant.LoadingState.SHOW)
            mView.onError(e)
        }
    }

    @Test
    fun `scanLocalStorage onSuccess`(){
        val loadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain

        every {
            bookRepository.localReadableObj.scanLocalStorage(context, capture(callbackBoolean))
        } answers {
            callbackBoolean.captured.onSuccess(true)
        }

        listBookLocalPresenter.scanLocalStorage(context, loadingArea)

        verify {
            mView.updateLoading(loadingArea, Constant.LoadingState.SHOW)
            mView.onScanBookComplete(true, loadingArea)
            mView.updateLoading(loadingArea, Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `scanLocalStorage onFailed`(){
        val loadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain
        val e = Exception()
        every {
            bookRepository.localReadableObj.scanLocalStorage(context, capture(callbackBoolean))
        } answers {
            callbackBoolean.captured.onError(e)
        }

        listBookLocalPresenter.scanLocalStorage(context, loadingArea)

        verify {
            mView.updateLoading(loadingArea, Constant.LoadingState.SHOW)
            mView.onError(e)
            mView.updateLoading(loadingArea, Constant.LoadingState.HIDE)
        }

    }

    @Test
    fun `deleteBookLocal onSuccess`(){

        val loadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain
        every {
            bookRepository.localWritableObj.deleteBookLocal(context, book, capture(callbackBoolean))
        } answers {
            callbackBoolean.captured.onSuccess(true)
        }

        listBookLocalPresenter.deleteBookLocal(context, book)

        verify {
            mView.updateLoading(loadingArea, Constant.LoadingState.SHOW)
            mView.onDeleteBookComplete(true)
            mView.updateLoading(loadingArea, Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `deleteBookLocal onError`(){

        val loadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain
        every {
            bookRepository.localWritableObj.deleteBookLocal(context, book, capture(callbackBoolean))
        } answers {
            callbackBoolean.captured.onError(e)
        }

        listBookLocalPresenter.deleteBookLocal(context, book)

        verify {
            mView.updateLoading(loadingArea, Constant.LoadingState.SHOW)
            mView.onError(e)
            mView.updateLoading(loadingArea, Constant.LoadingState.HIDE)
        }

    }

    @Test
    fun `searchBooksLocal onSuccess`(){

        val loadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain
        val keyword = "alice"
        every {
            bookRepository.localReadableObj.searchBookLocal(keyword, capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onSuccess(books)
        }

        listBookLocalPresenter.searchBooksLocal(keyword)

        verify {
            mView.onGetBooksSuccess(
                books.toBaseDataLocal(enableNextPage = true),
                loadingArea,
                DownloadFragment.Companion.GetBooksActionType.SILENT_REFRESH
            )
            mView.updateLoading(loadingArea, Constant.LoadingState.HIDE)
        }
    }
    @Test
    fun `searchBooksLocal empty keyword onSuccess`(){

        val loadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain
        val keyword = ""
        every {
            bookRepository.localReadableObj.searchBookLocal(keyword, capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onSuccess(books)
        }


        every {
            bookRepository.localReadableObj.getBooksLocal(offset = 0, listener = capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onSuccess(books)
        }

        listBookLocalPresenter.searchBooksLocal(keyword)

        verify {
            mView.onGetBooksSuccess(
                books.toBaseDataLocal(enableNextPage = true),
                Constant.LoadingAreaDownloadedBook.DownloadedBookMain,
                DownloadFragment.Companion.GetBooksActionType.REFRESH
            )
            mView.updateLoading(loadingArea, Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `searchBooksLocal onError`(){
        val loadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain
        val keyword = "alice"

        every {
            bookRepository.localReadableObj.searchBookLocal(keyword, capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onError(e)
        }

        listBookLocalPresenter.searchBooksLocal(keyword)

        verify {
            mView.onError(e)
            mView.updateLoading(loadingArea, Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `unzipBook onSuccess`(){
        val returnValue = "String"
        every {
            bookRepository.localWritableObj.unzipBook(context, book, capture(callbackString))
        } answers {
            callbackString.captured.onSuccess(returnValue)
        }

        listBookLocalPresenter.unzipBook(context, book)

        verify {
            mView.onUnzipBookSuccess(book)
        }
    }

    @Test
    fun `unzipBook onError`(){
        val returnValue = "String"
        every {
            bookRepository.localWritableObj.unzipBook(context, book, capture(callbackString))
        } answers {
            callbackString.captured.onError(e)
        }

        listBookLocalPresenter.unzipBook(context, book)

        verify {
            mView.onError(e)
            mView.onUnzipBookSuccess(book)
        }
    }

    @Test
    fun `parseEpubFile onSuccess`(){
        val path = "path"
        val epubFile = EpubFile()
        every {
            bookRepository.localReadableObj.parseEpub(path, book,  capture(callbackEpubFile))
        } answers {
            callbackEpubFile.captured.onSuccess(epubFile)
        }

        listBookLocalPresenter.parseEpubFile(book, path)

        Assert.assertEquals(book.epubFile, epubFile)
        verify {
            mView.onParseEpubDone(book.copy(prepared = 1), BasePresenter.Companion.Result.SUCCESS)
        }
    }

    @Test
    fun `parseEpubFile onError`(){
        val path = "path"
        every {
            bookRepository.localReadableObj.parseEpub(path, book,  capture(callbackEpubFile))
        } answers {
            callbackEpubFile.captured.onError(e)
        }

        listBookLocalPresenter.parseEpubFile(book, path)

        Assert.assertEquals(book.epubFile, null)
        verify {
            mView.onParseEpubDone(book, BasePresenter.Companion.Result.FAILED)
            mView.onError(e)        }
    }

    @Test
    fun `updateNewDataAndLoadMore onSuccess`(){
        val offset = 0
        every {
            bookRepository.localReadableObj.getBookWithLimit(
                offset + Constant.LOCAL_DATA_QUERY_PAGE_SIZE,
                capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onSuccess(books)
        }

        listBookLocalPresenter.updateNewDataAndLoadMore(0)

        verify {
            mView.onGetBooksSuccess(
                books.toBaseDataLocal(enableNextPage = true),
                loadingArea = Constant.LoadingArea.Common.NOT_SHOW_LOADING,
                action = DownloadFragment.Companion.GetBooksActionType.SILENT_REFRESH
            )                }
    }

    @Test
    fun `updateNewDataAndLoadMore onFailed`(){
        val offset = 0
        every {
            bookRepository.localReadableObj.getBookWithLimit(
                offset + Constant.LOCAL_DATA_QUERY_PAGE_SIZE,
                capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onError(e)
        }

        listBookLocalPresenter.updateNewDataAndLoadMore(0)

        verify {
            mView.onError(e)
        }
    }

    @Before
    fun setView(){
        listBookLocalPresenter.setView(mView)
    }
}
