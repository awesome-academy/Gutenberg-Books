package vn.ztech.software.projectgutenberg.data.model.epub

import vn.ztech.software.projectgutenberg.utils.Constant

data class TocItem(
    var bookId: Int = 0,
    var idx: Int = 0,
    var title: String = "",
    var href: String = "",
    var mimeType: String = "",
    var progress: String = "",
    var scrollYPosition: Int = 0,
    var isSlected: Boolean = false,
    var isLatestReading: Int = 0,
) {

    /**The progress will be a string and have the form of ***  currentPaget/totalPage  *****/
    fun progressStringToPercentage(): Int {
        if (progress.isEmpty()) return 0

        val twoNumbers = progress.split("/")
        return try {
            val firstNumber = Integer.parseInt(twoNumbers.first())
            val secondNumber = Integer.parseInt(twoNumbers[1])
            ((firstNumber + 1) * Constant.ONE_HUNDRED_PERCENT) / secondNumber
        } catch (e: java.lang.NumberFormatException) {
            0
        }
    }

    fun getCurrentReadingPage(): Pair<Int, Int> {
        if (progress.isEmpty()) return Pair(0, 0)

        val twoNumbers = progress.split("/")
        return try {
            Pair(twoNumbers.first().toInt(), twoNumbers[1].toInt())
        } catch (e: java.lang.NumberFormatException) {
            Pair(0, 0)
        }
    }

    fun isDone(): Boolean {
        val twoNumbers = getTwoNumberOfProgress()
        if (twoNumbers.isEmpty()) return false
        return twoNumbers.first() + 1 == twoNumbers[1]
    }

    private fun getTwoNumberOfProgress(): List<Int> {
        val twoNumbers = progress.split("/")
        return try {
            listOf<Int>(
                Integer.parseInt(twoNumbers.first()),
                Integer.parseInt(twoNumbers[1])
            )

        } catch (e: java.lang.NumberFormatException) {
            emptyList<Int>()
        }
    }

    fun getProgressToDisplay(): String {
        if (progress.isEmpty()) return progress
        val twoNumbers = progress.split("/")
        val firstNumber = Integer.parseInt(twoNumbers.first())
        val secondNumber = Integer.parseInt(twoNumbers[1])
        return "${firstNumber + 1}/$secondNumber"
    }

    companion object {
        const val STRING_COVER = "Cover"
    }
}
