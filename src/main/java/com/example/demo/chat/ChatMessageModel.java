package com.example.demo.chat;

public class ChatMessageModel {
    private String sender;
    private String content;
    private String gameId;

    public ChatMessageModel(String sender, String content, String gameId) {
        this.sender = sender;
        this.content = content;
        this.gameId = gameId;
    }

    // Getters and setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
