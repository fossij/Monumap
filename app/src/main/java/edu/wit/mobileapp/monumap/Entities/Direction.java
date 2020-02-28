package edu.wit.mobileapp.monumap.Entities;

public enum Direction {
    NONE (""),
    LEFT ("left"),
    RIGHT ("right"),
    STRAIGHT ("straight"),
    STAIRS ("stairs"),
    ELEVATOR ("elevator");

    private String text;

    Direction(String text) {
        this.text = text;
    }
}
