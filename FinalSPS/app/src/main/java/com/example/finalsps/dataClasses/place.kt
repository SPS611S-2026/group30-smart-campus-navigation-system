package com.example.finalsps.dataClasses

data class Place(
    val id: Int,
    val name: String,
    val description: String,
    val roomNumber: String,
    val floor: String,
    val building: String,
    val lecturer: String,
    val courseCode: String,
    val latitude: Double,
    val longitude: Double,
    val directions: List<String>
)