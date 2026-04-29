package com.example.finalsps.dataClasses

data class Place(
    val id: Int=0,
    val name: String= "",
    val description: String="",
    val roomNumber: String="",
    val floorNumber: String="",
    val building: String="",
    val lecturer: String="",
    val courseCode: String="",
    val latitude: Double=0.0,
    val longitude: Double=0.0,
    val directions: List<String> = emptyList()
)

