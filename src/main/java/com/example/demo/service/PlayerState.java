package com.example.demo.service;

import com.example.demo.models.Ship;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerState {
    private final ConcurrentHashMap<String, List<Ship>> playersShips = new ConcurrentHashMap<>();

    public void addPlayerShips(String playerName, List<Ship> ships) {
        playersShips.put(playerName, ships);
    }

    public boolean isSetupComplete() {
        return playersShips.size() == 2; // Ensure two players have sent their ships
    }

    public ConcurrentHashMap<String, List<Ship>> getPlayersShips() {
        return playersShips;
    }
}
