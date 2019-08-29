package com.soteria.neurolab.models;

public class GameSession {

    private int sessionID;
    private int patientID;
    private int gameID;
    private double metrics;
    private String date;

    public GameSession(int patientID, int gameID, double metrics, String date) {
        this.patientID = patientID;
        this.gameID = gameID;
        this.metrics = metrics;
        this.date = date;
    }

    public GameSession(){}

    public int getSessionID() {
        return sessionID;
    }

    public void setSessionID(int sessionID) {
        this.sessionID = sessionID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public double getMetrics() {
        return metrics;
    }

    public void setMetrics(double metrics) {
        this.metrics = metrics;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
