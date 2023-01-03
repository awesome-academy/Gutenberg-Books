package vn.ztech.software.projectgutenberg

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository
import vn.ztech.software.projectgutenberg.screen.home.ListBookContract
import vn.ztech.software.projectgutenberg.screen.home.ListBookPresenter
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.extension.toBaseData

class ListBookPresenterTest {

    private val mView = mockk<ListBookContract.View>(relaxed = true)
    private val bookRepository = mockk<BookRepository>()
    private val listBookPresenter = ListBookPresenter(bookRepository)
    private val callback = slot<OnResultListener<BaseAPIResponse<Book>>>()
    private val callbackListBookLocal = slot<OnResultListener<List<BookLocal>>>()

    private val e = mockk<Exception>()
    @Test
    fun `getBooks onSuccess`(){
        val res = BaseAPIResponse(
            1,
            results = listOf(Book(emptyList(), emptyList(), "", 1, 1, emptyList(), "", emptyList(),
            emptyList(),""))
        )

        every {
            bookRepository.remoteReposObj.getBooks(0, listener = capture(callback))
        } answers {
            callback.captured.onSuccess(res)
        }

        listBookPresenter.getBooks(0)

        Assert.assertEquals(listBookPresenter.isLastPage, true)
        verify {
            mView.onGetBooksSuccess(res.toBaseData(), Constant.LoadingAreaHome.HomeListBook)
            mView.updateLoading(Constant.LoadingAreaHome.HomeListBook, Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `getDownloadedBooks onError`(){

        every {
            bookRepository.remoteReposObj.getBooks(0, capture(callback))
        } answers {
            callback.captured.onError(e)
        }

        listBookPresenter.getBooks(0)
        verify {
            mView.updateLoading(Constant.LoadingAreaHome.HomeListBook, Constant.LoadingState.HIDE)
            mView.onError(e)
        }
    }

    @Test
    fun `getBooksWithFilters onSuccess`(){
        val res = BaseAPIResponse(
            1,
            results = listOf(Book(emptyList(), emptyList(), "", 1, 1, emptyList(), "", emptyList(),
                emptyList(),""))
        )
        val filters = mapOf<BookDataSource.Companion.BookFilter, String>(
            BookDataSource.Companion.BookFilter.HAS_BOOKSHELF to "novel"
        )
        every {
            bookRepository.remoteReposObj.getBooksWithFilters(
                0,
                filters,
                listener = capture(callback))
        } answers {
            callback.captured.onSuccess(res)
        }

        listBookPresenter.getBooksWithFilters(0, filters)

        Assert.assertEquals(listBookPresenter.isLastPage, true)
        verify {
            mView.onGetBooksSuccess(res.toBaseData(),
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthor)
            mView.updateLoading(Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthor,
                Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `getBooksWithFilters onError`(){

        val filters = mapOf<BookDataSource.Companion.BookFilter, String>(
            BookDataSource.Companion.BookFilter.HAS_BOOKSHELF to "novel"
        )
        every {
            bookRepository.remoteReposObj.getBooksWithFilters(
                0,
                filters,
                listener = capture(callback))
        } answers {
            callback.captured.onError(e)
        }

        listBookPresenter.getBooksWithFilters(0, filters)

        Assert.assertEquals(listBookPresenter.isLastPage, false)
        verify {
            mView.updateLoading(Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthor,
                Constant.LoadingState.HIDE)
            mView.onError(e)
        }
    }

    @Test
    fun `getAllRecentReadingBook onSuccess`(){
        val res = listOf<BookLocal>(
            BookLocal(
                title = "",
                displayName = "",
                size = 0L,
                data = "",
                dateModified = 0L,
                mimeType = "")
        )

        every {
            bookRepository.localReadableObj.getAllRecentReadingBook(capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onSuccess(res)
        }

        listBookPresenter.getAllRecentReadingBook()

        verify {
            mView.onGetRecentReadingBookSuccess(res, Constant.LoadingAreaHome.HomeRecentReading,)
            mView.updateLoading(Constant.LoadingAreaHome.HomeRecentReading, Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `getAllRecentReadingBook onError`(){

        every {
            bookRepository.localReadableObj.getAllRecentReadingBook(capture(callbackListBookLocal))
        } answers {
            callbackListBookLocal.captured.onError(e)
        }

        listBookPresenter.getAllRecentReadingBook()

        verify {
            mView.updateLoading(Constant.LoadingAreaHome.HomeRecentReading, Constant.LoadingState.HIDE)
            mView.onError(e)
        }
    }
    @Before
    fun setView(){
        listBookPresenter.setView(mView)
    }
}
