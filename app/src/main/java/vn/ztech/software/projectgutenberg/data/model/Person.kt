package vn.ztech.software.projectgutenberg.data.model

data class Person(
    val alias: String,
    val birthDate: String,
    val deathDate: String,
    val name: String,
    val webpage: String
)

object PersonEntry {
    const val ALIAS = "alias"
    const val BIRTH_DATE = "birth_date"
    const val DEATH_DATE = "death_date"
    const val NAME = "name"
    const val WEBPAGE = "webpage"
}
