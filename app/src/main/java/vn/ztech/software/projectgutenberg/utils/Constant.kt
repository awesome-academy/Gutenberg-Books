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

    enum class LoadingArea {
        HomeRecentReading,
        HomeBookshelf,
        HomeListBook,
        HomeLoadMoreBook,
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
    const val FIRST_PAGE = 1
}
