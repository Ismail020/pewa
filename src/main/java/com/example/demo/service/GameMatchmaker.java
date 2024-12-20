package com.example.demo.service;

import com.example.demo.models.Game;
import com.example.demo.models.GameRepository;
import com.example.demo.models.GameState;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class GameMatchmaker {
    private final Queue<String> waitingPlayers = new LinkedList<>();
    private final GameRepository gameRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final GameService gameService; // inject GameService


    public GameMatchmaker(GameRepository gameRepository, SimpMessagingTemplate messagingTemplate, GameService gameService) {
        this.gameRepository = gameRepository;
        this.messagingTemplate = messagingTemplate;
        this.gameService = gameService; // initialize gameService
    }

    public void addPlayerToQueue(String username) {
        waitingPlayers.add(username);
        if (waitingPlayers.size() >= 2) {
            startGame();
        }
    }

    public void removePlayerFromQueue(String username) {
        waitingPlayers.remove(username);
    }

    public boolean isPlayerInQueue(String username) {
        return waitingPlayers.contains(username);
    }

    private void startGame() {
        String player1 = waitingPlayers.poll();
        String player2 = waitingPlayers.poll();

        if (player1 == null || player2 == null) {
            System.out.println("Not enough players to start a game.");
            return;
        }

        //start game by initializing the gamestate and registering it with the gameservice.
        Game game = new Game(player1, player2);
        gameService.initializeGame(game);
        game.setGameState(new GameState(player1, player2));
        game.getGameState().setInProgress(true);


        Game savedGame = gameRepository.save(game);

        System.out.println("Game started: ID = " + savedGame.getId() + " Player 1 = " + player1 + " and Player 2 = " + player2);
        System.out.println("Game saved with ID: " + savedGame.getId());

        System.out.println(player1 + " is playing game " + savedGame.getId());
        System.out.println(player2 + " is playing game " + savedGame.getId());

        messagingTemplate.convertAndSendToUser(savedGame.getPlayer1(), "/queue/game", game);
        messagingTemplate.convertAndSendToUser(savedGame.getPlayer2(), "/queue/game", game);

        System.out.println("Notified players of game start.");


    }
}

