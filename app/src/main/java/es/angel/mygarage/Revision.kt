package es.angel.mygarage



data class Revision(
    val id: Int = 0,
    val plate: String,
    val description: String,
    val title: String,
    val workshop: String,
    val date: String
)
