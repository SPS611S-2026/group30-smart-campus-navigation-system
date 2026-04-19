data class Room(
    val id: String,
    val roomNumber: String,
    val buildingName: String,
    val floor: String,
    val lecturerName: String,
    val courseCode: String,
    val directions: List<String>,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isBookmarked: Boolean = false
)