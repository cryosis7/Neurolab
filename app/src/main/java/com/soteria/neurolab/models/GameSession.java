package com.soteria.neurolab.models;

import com.soteria.neurolab.utilities.DateManager;

import java.util.Date;

public class GameSession {

    private int sessionID;
    private int patientID;
    private int gameID;
    private double metrics;
    private String date;

    /**
     * A class that represents a game session stored in the database.
     * @param patientID The database referenced ID of the patient
     * @param gameID The ID of the game
     * @param metrics The score from the game
     * @param date The date of the session. Use DateManager.getDateString
     */
    public GameSession(int patientID, int gameID, double metrics, String date) {
        this.patientID = patientID;
        this.gameID = gameID;
        this.metrics = metrics;
        this.date = date;
    }

    /**
     * A class that represents a game session stored in the database.
     * @param patientID The database referenced ID of the patient
     * @param gameID The ID of the game
     * @param metrics The score from the game
     * @param date The date of the session.
     */
    public GameSession(int patientID, int gameID, double metrics, Date date) {
        this.patientID = patientID;
        this.gameID = gameID;
        this.metrics = metrics;
        this.date = DateManager.getDateString(date);
    }

    public GameSession(){}

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

    public void setDate(Date date) { this.date = DateManager.getDateString(date); }
}
