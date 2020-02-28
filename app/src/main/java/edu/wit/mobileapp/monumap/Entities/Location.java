package edu.wit.mobileapp.monumap.Entities;

public class Location {
    private String building;
    private int floor;
    private int room;

    public Location(String building, int floor, int room) {
        this.building = building;
        this.floor = floor;
        this.room = room;
    }

    public String getLocationName() {
        return this.building + this.room;
    }

    public String getBuilding() {
        return this.building;
    }

    public int getFloor() {
        return this.floor;
    }

    public int getRoom() {
        return this.room;
    }
}
