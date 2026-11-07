package es.angel.mygarage

data class Vehicle(
    val id: Int = 0,
    val plate: String,
    val brand: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val userId: Int
)