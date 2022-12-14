package vn.ztech.software.projectgutenberg.utils.extension

import vn.ztech.software.projectgutenberg.data.model.Agent
import vn.ztech.software.projectgutenberg.data.model.AgentEntry
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.utils.Constant

/**
 * Find the author in this list of agent.
 * If can not find the author, chose the first agent
 * */
fun List<Agent>.findShowableAgent(): Agent? {
    return if (this.isEmpty()) null
    else {
        this.forEach {
            if (it.type == AgentEntry.TYPE_AUTHOR) return it
        }
        this.first()
    }
}

/** Find an image with smallest size in a list of multiple types of resource,
 *  but prioritize the image with medium size because it fits the best with mobile devies.  */
fun List<Resource>.findCoverImageURL(): String? {
    return if (this.isEmpty()) null
    else {
        var smallestSizeOrdinal: Int = Constant.MAX_IMAGE_SIZE_ORDINAL
        var smallestSizeImageUrl: String? = null

        this.forEach { resource ->
            val result = findSizeOfThisImage(resource, smallestSizeOrdinal) ?: return@forEach
            if (!result.first) {
                smallestSizeOrdinal = result.second.first
                smallestSizeImageUrl = result.second.second
            } else {
                return result.second.second
            }
        }
        smallestSizeImageUrl
    }
}

private fun findSizeOfThisImage(
    resource: Resource,
    smallestSizeOrdinal: Int
): Pair<Boolean, Pair<Int, String>>? {
    return if (resource.type.contains(Constant.IMAGE_STRING)) {
        val uri = resource.uri
        getBestImageSize(uri, smallestSizeOrdinal)
    } else null
}

private fun getBestImageSize(
    uri: String,
    smallestSizeOrdinal: Int
): Pair<Boolean, Pair<Int, String>>? {
    var result = Pair(false, Pair(Constant.ImageSize.SMALL.ordinal, uri))
    Constant.ImageSize.values().forEach { imageSize ->
        if (uri.contains(imageSize.nameStr)) {
            if (imageSize.ordinal < smallestSizeOrdinal) result =
                Pair(false, Pair(imageSize.ordinal, uri))
            /** whenever reach an image with medium size, return immediately*/
            if (imageSize == Constant.ImageSize.MEDIUM) return Pair(
                true,
                Pair(imageSize.ordinal, uri)
            )
        }
    }
    return result
}

fun Book.getAvailableMetadata(): String {
    val languages = this.getLanguages()
    return "" +
            (if (languages != null) "Language: $languages" else "") +
            " ♦ Downloads: ${this.downloads}"
}

private fun Book.getLanguages(): String? {
    if (this.languages.isEmpty()) return null
    return this.languages.fold("") { language, acc ->
        acc + language
    }
}

