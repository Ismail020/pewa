package com.example.demo.models;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class Game {


    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id; // gonna make it be checked in the by the database.
    public void setId(int id) {};

    @Setter
    private String player1;
    @Setter
    private String player2; // what if it's a bot? implement later.
    @Setter
    @Embedded
    private GameState gameState;

    public Game() {
    }

    public Game(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    //later functionality for
}
