package com.example.demo.models;

import org.springframework.web.socket.WebSocketSession;


public class Game {
    private int Id; // gonna make it be checked in the by the database.
    private WebSocketSession player1;
    private WebSocketSession player2; // what if it's a bot? implement later.
    private GameState gameState;

    public Game(WebSocketSession player1, WebSocketSession player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameState = new GameState();
    }

    public int getId() {
        return Id;
    }

    public WebSocketSession getPlayer1() {
        return player1;
    }

    public void setPlayer1(WebSocketSession player1) {
        this.player1 = player1;
    }

    public WebSocketSession getPlayer2() {
        return player2;
    }

    public void setPlayer2(WebSocketSession player2) {
        this.player2 = player2;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }



    //later functionality for
}
