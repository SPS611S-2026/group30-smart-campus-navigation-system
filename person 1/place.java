package com.nust.smartnavigate.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a location on the NUST campus.
 * Each Place has an ID, name, coordinates, description,
 * room number, and floor number.
 */
public class Place {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private String roomNumber;
    private String floorNumber;

    // Constructor
    public Place(int id, String name, double latitude, double longitude,
                 String description, String roomNumber, String floorNumber) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.roomNumber = roomNumber;
        this.floorNumber = floorNumber;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDescription() { return description; }
    public String getRoomNumber() { return roomNumber; }
    public String getFloorNumber() { return floorNumber; }

    // Setters (future-proofing for changes)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setDescription(String description) { this.description = description; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setFloorNumber(String floorNumber) { this.floorNumber = floorNumber; }

    // For debugging: print Place details
    @Override
    public String toString() {
        return name + " (" + roomNumber + ", Floor " + floorNumber + ") - " + description;
    }

    // MAIN METHOD for testing
    public static void main(String[] args) {
        // Create a list of sample places
        List<Place> places = new ArrayList<>();
        places.add(new Place(1, "Prof. Fabianus Ihemba Office", -22.5591, 17.0835,
                "Software Development Lecturer Office", "Room 101", "1"));
        places.add(new Place(2, "Dr. Waza Milijala Office", -22.5592, 17.0836,
                "Software Development Lecturer Office", "Room 102", "1"));
        places.add(new Place(3, "Mr. Shipoke Isaskar Office", -22.5593, 17.0837,
                "Software Development Lecturer Office", "Room 103", "1"));
        places.add(new Place(4, "Mr. Elikkan N Rott Office", -22.5594, 17.0838,
                "Software Development Lecturer Office", "Room 104", "1"));

        // Print them out
        System.out.println(" NUST Campus Places:");
        for (Place p : places) {
            System.out.println(p);
        }

        // Demonstrate setters: update Elikkan's office room
        Place myOffice = places.get(3); // index 3 = Elikkan
        System.out.println("\nBefore update: " + myOffice);
        myOffice.setRoomNumber("Room 202");
        System.out.println("After update: " + myOffice);
    }
}
