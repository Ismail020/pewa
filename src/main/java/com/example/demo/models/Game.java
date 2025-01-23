package com.example.demo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
public class Game {


    public int getId() {
        return Id;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public Set<Integer> getPlayer1Locations() {
        return player1Locations;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public Set<Integer> getPlayer2Locations() {
        return player2Locations;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id; // gonna make it be checked in the by the database.

    public void setId(int id) {
    }

    @Setter
    private String player1;

    @Getter
    @Transient
    private final Set<Integer> player1Locations = new HashSet<>();

    @Getter
    @Transient
    private final Set<Integer> player1HitsTaken = new HashSet<>();

    @Getter
    @Transient
    private final Set<Integer> player1HitsDealt = new HashSet<>();

    @Getter
    @Transient
    private final Set<Integer> player1ShotsFired = new HashSet<>();


    private String player2;

    @Getter
    @Transient
    private final Set<Integer> player2Locations = new HashSet<>();

    @Getter
    @Transient
    private final Set<Integer> player2HitsTaken = new HashSet<>();

    @Getter
    @Transient
    private final Set<Integer> player2HitsDealt = new HashSet<>();

    @Getter
    @Transient
    private final Set<Integer> player2ShotsFired = new HashSet<>();

    @Setter
    @Embedded
    private GameState gameState;

    public Game() {
    }

    public Game(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Adds the specified locations to the set of player 1's locations.
     *
     * @param locations a list of integers representing the locations to add for player 1
     */
    public void addPlayer1Locations(List<Integer> locations) {
        player1Locations.addAll(locations);
    }

    /**
     * Adds the specified locations to the set of player 2's locations.
     *
     * @param locations a list of integers representing the locations to add for player 2
     */
    public void addPlayer2Locations(List<Integer> locations) {
        player2Locations.addAll(locations);
    }

}
