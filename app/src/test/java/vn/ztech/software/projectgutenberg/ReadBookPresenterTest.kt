package vn.ztech.software.projectgutenberg

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository
import vn.ztech.software.projectgutenberg.screen.readbook.ReadBookContract
import vn.ztech.software.projectgutenberg.screen.readbook.ReadBookPresenter
import vn.ztech.software.projectgutenberg.utils.Constant

class ReadBookPresenterTest {

    private val view = mockk<ReadBookContract.View>(relaxed = true)
    private val bookRepository = mockk<BookRepository>()
    private val readBookPresenter = ReadBookPresenter(bookRepository)
    private val callback = slot<OnResultListener<Toc>>()
    private val callBackBoolean = slot<OnResultListener<Boolean>>()
    private val exception = mockk<Exception>()

    @Test
    fun `get Toc callback return on Success`(){
        val toc = Toc(mutableListOf(TocItem()))
        every {
            bookRepository.localReadableObj.getToc(0, capture(callback))
        } answers {
            callback.captured.onSuccess(toc)
        }

        readBookPresenter.getToc(0, Constant.LoadingAreaReadBook.ReadBookMain)

        Assert.assertEquals(toc.listItem.first().title, TocItem.STRING_COVER)
        verify {
            view.onGetTocSuccess(toc, Constant.LoadingAreaReadBook.ReadBookMain) // default loading area
            view.updateLoading(Constant.LoadingAreaReadBook.ReadBookMain, Constant.LoadingState.HIDE)
        }
    }

    @Test
    fun `get Toc callback return on Failed`(){
        every {
            bookRepository.localReadableObj.getToc(0, capture(callback))
        } answers {
            callback.captured.onError(exception)
        }

        readBookPresenter.getToc(0, Constant.LoadingAreaReadBook.ReadBookMain)
        verify {
            view.updateLoading(Constant.LoadingAreaReadBook.ReadBookMain, Constant.LoadingState.HIDE)
            view.onError(exception) // default loading area
        }
    }

    @Test
    fun `update Reading Progress callback return on Success`(){
        val tocItem = TocItem(href = "fake_href")

        every {
            bookRepository.localWritableObj.updateReadingProgress(0, "fake_href", "", capture(callBackBoolean))
        } answers {
            callBackBoolean.captured.onSuccess(true)
        }

        readBookPresenter.updateReadingProgress(tocItem, "",)
        verify {
            view.onUpdateReadingProgressDone(tocItem) // default loading area
        }
    }

    @Test
    fun `update Reading Progress callback return on Error`(){
        val tocItem = TocItem(href = "fake_href")

        every {
            bookRepository.localWritableObj.updateReadingProgress(0, "fake_href", "", capture(callBackBoolean))
        } answers {
            callBackBoolean.captured.onError(exception)
        }

        readBookPresenter.updateReadingProgress(tocItem, "",)
    }

    @Test
    fun `updateLatestReadingTocItem`() {
        val tocItem = TocItem(href = "fake_href")

        every {
            bookRepository.localWritableObj.updateLatestReadingTocItem(tocItem)
        } answers {
        }

        readBookPresenter.updateLatestReadingTocItem(tocItem,)
    }


    @Before
    fun setView(){
        readBookPresenter.setView(view)
    }
}
