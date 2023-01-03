package vn.ztech.software.projectgutenberg

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.bookshelf.BookshelfRepository
import vn.ztech.software.projectgutenberg.screen.home.ListBookshelfContract
import vn.ztech.software.projectgutenberg.screen.home.ListBookshelfPresenter
import vn.ztech.software.projectgutenberg.utils.Constant

class ListBookshelfPresenterTest {

    private val mView = mockk<ListBookshelfContract.View>(relaxed = true)
    private val bookshelfRepository = mockk<BookshelfRepository>()
    private val bookshelfPresenter = ListBookshelfPresenter(bookshelfRepository)
    private val callback = slot<OnResultListener<BaseAPIResponse<Bookshelf>>>()
    private val e = mockk<Exception>()

    @Test
    fun `getBookshelves onSuccess`(){
        val res = BaseAPIResponse(1, results = listOf(Bookshelf(0, "Novel")))
        val page = (Math.random() * Constant.PAGE_NUMBER_RANDOM_RANGE).toInt() + Constant.FIRST_PAGE
        every {
            bookshelfRepository.getBookshelves(
                page,
                capture(callback))
        } answers {
            callback.captured.onSuccess(res)
        }

        bookshelfPresenter.getBookshelves()

        verify {
            mView.onGetBookshelvesSuccess(res.results)
            mView.updateLoading(
                Constant.LoadingAreaHome.HomeBookshelf,
                Constant.LoadingState.HIDE
            )
        }
    }

    @Test
    fun `getBookshelves onError`(){
        val page = (Math.random() * Constant.PAGE_NUMBER_RANDOM_RANGE).toInt() + Constant.FIRST_PAGE
        every {
            bookshelfRepository.getBookshelves(
                page,
                capture(callback))
        } answers {
            callback.captured.onError(e)
        }

        bookshelfPresenter.getBookshelves()

        verify {
            mView.updateLoading(
                Constant.LoadingAreaHome.HomeBookshelf,
                Constant.LoadingState.HIDE
            )
            mView.onError(e)
        }
    }


    @Before
    fun setView(){
        bookshelfPresenter.setView(mView)
    }
}
