package com.soteria.neurolab.models;

public class GameAssignment {

    private int gameID;
    private int patientID;
    private int gameAttempts;

    public GameAssignment(int gameID, int patientID, int gameAttempts) {
        this.gameID = gameID;
        this.patientID = patientID;
        this.gameAttempts = gameAttempts;
    }

    public GameAssignment(){}

    public int getGameAttempts() {
        return gameAttempts;
    }

    public void setGameAttempts(int gameAttempts) {
        this.gameAttempts = gameAttempts;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }
}
