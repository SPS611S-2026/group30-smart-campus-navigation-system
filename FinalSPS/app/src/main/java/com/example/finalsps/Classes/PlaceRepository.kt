package com.example.finalsps.dataClasses

object PlaceRepository {

    val places = listOf(
        Place(
            id = 1,
            name = "Prof. Fabianus Ihemba Office",
            description = "Software Development Lecturer Office",
            roomNumber = "Room 101",
            floor = "1",
            building = "Engineering Block",
            lecturer = "Prof. Fabianus Ihemba",
            courseCode = "SPS611S",
            latitude = -22.5591,
            longitude = 17.0835,
            directions = listOf(
                "Enter Engineering Building",
                "Go to first floor",
                "Room 101 is on the left"
            )
        ),
        Place(
            id = 2,
            name = "Dr. Waza Milijala Office",
            description = "Software Development Lecturer Office",
            roomNumber = "Room 102",
            floor = "1",
            building = "Engineering Block",
            lecturer = "Dr. Waza Milijala",
            courseCode = "SPS611S",
            latitude = -22.5592,
            longitude = 17.0836,
            directions = listOf(
                "Enter Engineering Building",
                "Go to first floor",
                "Room 102 is next to Room 101"
            )
        )
    )

    fun search(query: String): List<Place> {
        return places.filter {
            it.name.contains(query, true) ||
                    it.roomNumber.contains(query, true) ||
                    it.description.contains(query, true)
        }
    }

    fun getById(id: Int): Place? {
        return places.find { it.id == id }
    }
}