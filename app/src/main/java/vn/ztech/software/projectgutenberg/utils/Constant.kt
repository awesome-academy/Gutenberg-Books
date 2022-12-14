package vn.ztech.software.projectgutenberg.utils

object Constant {
    const val SPLASH_DELAY_TIME = 1500L
    const val NUM_SCREEN = 4
    const val STRING_NULL = "null"

    enum class ScreenNumber {
        Home,
        Shelf,
        Download,
        Favorite
    }

    interface LoadingArea {
        enum class Common : LoadingArea {
            NOT_SHOW_LOADING
        }
    }

    enum class LoadingAreaHome : LoadingArea {
        HomeRecentReading,
        HomeBookshelf,
        HomeListBook,
        HomeLoadMoreBook,
    }

    enum class LoadingAreaBookDetail : LoadingArea {
        BookDetailsListWithSameAuthor,
        BookDetailsListWithSameAuthorMore,
        BookDetailsListWithSameBookshelf,
        BookDetailsListWithSameBookshelfMore,
    }

    enum class LoadingAreaDownloadedBook : LoadingArea {
        DownloadedBookMain,
        DownloadedBookLoadMore,
    }

    enum class LoadingAreaReadBook : LoadingArea {
        ReadBookMain,
    }

    enum class LoadingState {
        SHOW,
        HIDE
    }

    enum class ImageSize(nameStr: String) {
        SMALL("small"), MEDIUM("medium");

        val nameStr = nameStr
    }

    const val IMAGE_STRING = "image"
    const val MAX_IMAGE_SIZE_ORDINAL = 100
    const val PAGE_NUMBER_RANDOM_RANGE = 10

    enum class ResourceKindClues(
        val nameLowerCase: String,
        val fullName: String,
        val actionType: ActionTypes
    ) {
        EPUB("epub", "epub+zip", ActionTypes.DOWNLOAD),
        MOBI("mobi", "mobi", ActionTypes.DOWNLOAD),
        PLAIN("plain", "plain", ActionTypes.PREVIEW),
        HTML("html", "html", ActionTypes.PREVIEW),
        JPEG("jpeg", "jpeg", ActionTypes.DEFAULT),
        RDF("rdf", "rdf", ActionTypes.DEFAULT),
        ZIP("zip", ".zip", ActionTypes.DEFAULT),
    }
    val supportedResources = listOf(ResourceKindClues.EPUB)
    enum class ActionTypes {
        PREVIEW, DOWNLOAD, DEFAULT
    }

    const val FIRST_PAGE = 1
    const val DEFAULT_OFFSET = 0
    const val EMPTY_STRING = ""

    const val DOWNLOAD_UPDATE_PROGRESS_SLEEP_TIME = 100L
    const val URI_FORMAT_REGEX = "[^a-zA-Z0-9.-/]"
    const val URI_FORMAT_REPLACEMENT = "_"
    const val URI_FORMAT_REPLACEMENT_SPACE = " "
    const val LOCAL_DATA_QUERY_PAGE_SIZE = 6

    const val CONTENT_PROVIDER_BOOK_STORAGE = "external"
    const val CONTENT_PROVIDER_BOOK_QUERY_LIKE_COMMAND = " LIKE ? "
    const val CONTENT_PROVIDER_BOOK_QUERY_EQUAL_COMMAND = " = ? "
    const val CONTENT_PROVIDER_BOOK_QUERY_LIKE_PATTERN = "%"

    const val SIZE_1KB = 1024
    const val SIZE_1MB = 1024 * 1024
    const val SIZE_1KB_STR = "KB"
    const val SIZE_1MB_STR = "MB"

    const val UNZIPPED_FOLDER_NAME = "Unzipped"
    const val ZIPPED_EBOOK_FOLDER = "Ebook"
    const val UTF_8 = "UTF-8"

    const val ONE_HUNDRED_PERCENT = 100
}
