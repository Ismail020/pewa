package com.example.demo.models;

import java.util.List;

public class Ship {
    private String name;
    private int size;
    private List<Integer> locations;  // Or any other data type for locations
    private boolean placed;

    // Constructor, getters, and setters
    public Ship(String name, int size, List<Integer> locations, boolean placed) {
        this.name = name;
        this.size = size;
        this.locations = locations;
        this.placed = placed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Integer> getLocations() {
        return locations;
    }

    public void setLocations(List<Integer> locations) {
        this.locations = locations;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
}
