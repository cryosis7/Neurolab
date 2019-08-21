package com.soteria.neurolab.Models;

public class Game {

    private int gameID;
    private String gameName;
    private String gameDesc;

    public Game(String gameName, String gameDesc) {
        this.gameName = gameName;
        this.gameDesc = gameDesc;
    }

    public Game(){}

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) { this.gameID = gameID; }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameDesc() {
        return gameDesc;
    }

    public void setGameDesc(String gameDesc) {
        this.gameDesc = gameDesc;
    }
}
