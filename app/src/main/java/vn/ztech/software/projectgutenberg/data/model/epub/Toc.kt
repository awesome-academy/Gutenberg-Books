package vn.ztech.software.projectgutenberg.data.model.epub

import vn.ztech.software.projectgutenberg.utils.Constant

data class Toc(var listItem: MutableList<TocItem> = mutableListOf()) {

    fun getTotalPercentage(): Int {
        val numOfToc = listItem.size
        var sumPercentage = 0
        listItem.forEach { tocItem ->
            val progressPair = getPercentagePair(tocItem.progress)
            progressPair?.let {
                sumPercentage += (it.first * Constant.ONE_HUNDRED_PERCENT / it.second)
            }
        }
        return (sumPercentage) / (numOfToc)
    }

    private fun getPercentagePair(progress: String): Pair<Int, Int>? {
        if (progress.isEmpty()) return null
        val pairStr = progress.split("/")
        return Pair(Integer.parseInt(pairStr.first()), Integer.parseInt(pairStr[1]))
    }
}
