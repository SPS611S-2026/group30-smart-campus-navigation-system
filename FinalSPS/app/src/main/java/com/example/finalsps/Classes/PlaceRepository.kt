package com.example.finalsps.dataClasses

object PlaceRepository {

    val places = listOf(
        Place(
            1, "Prof. Fabianus Ihemba Office",
            -22.5591, 17.0835,
            "Software Development Lecturer Office",
            "Room 101", "1"
        ),
        Place(
            2, "Dr. Waza Milijala Office",
            -22.5592, 17.0836,
            "Software Development Lecturer Office",
            "Room 102", "1"
        ),
        Place(
            3, "Mr. Shipoke Isaskar Office",
            -22.5593, 17.0837,
            "Software Development Lecturer Office",
            "Room 103", "1"
        ),
        Place(
            4, "Mr. Elikkan N Rott Office",
            -22.5594, 17.0838,
            "Software Development Lecturer Office",
            "Room 104", "1"
        )
    )

    fun search(query: String): List<Place> {
        return places.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.roomNumber.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }

    fun getById(id: Int): Place? {
        return places.find { it.id == id }
    }
}