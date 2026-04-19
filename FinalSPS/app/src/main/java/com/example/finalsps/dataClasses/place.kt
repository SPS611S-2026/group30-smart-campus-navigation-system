package com.example.finalsps.dataClasses

data class Place(
    val id: String,
    val name: String,
    val roomNumber: String,
    val floor: String,
    val building: String,
    val lecturer: String,
    val courseCode: String,
    val latitude: Double,
    val longitude: Double,
    val directions: List<String>
)