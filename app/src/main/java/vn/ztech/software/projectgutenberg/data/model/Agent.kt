package vn.ztech.software.projectgutenberg.data.model

data class Agent(
    val id: Int,
    val person: String,
    val type: String
)

object AgentEntry {
    const val ID = "id"
    const val PERSON = "person"
    const val TYPE = "type"
}
