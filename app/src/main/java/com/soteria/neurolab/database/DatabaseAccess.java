package com.soteria.neurolab.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.soteria.neurolab.Models.Game;
import com.soteria.neurolab.Models.GameAssignment;
import com.soteria.neurolab.Models.GameSession;
import com.soteria.neurolab.Models.Patient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor cursor = null;

    public DatabaseAccess(Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) throws SQLiteException {
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Opens the database
     * @throws SQLiteException
     */
    public  void open() throws SQLiteException{
        try {
            this.db = openHelper.getWritableDatabase();
        } catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    /**
     * Closes the database
     */
    public void close(){
        if(db != null){
            this.db.close();
        }
    }

    //--------------------------------Patient Methods--------------------------------------//

    /**
     *Inserts a patient object into the database
     * @param patient The patient object to be inserted
     * @throws SQLiteException
     */
    public void createPatient(Patient patient) throws SQLiteException{
        open();
        ContentValues values = new ContentValues();
        values.put("patient_reference", patient.getPatientReference());
        db.insert("Patient", null, values);
        close();
    }

    /**
     * Retrieves a list of all patients from the database
     * @return a list of patients
     * @throws SQLiteException
     */
    public List<Patient> getAllPatients() throws SQLiteException{
        open();
        List<Patient> patientList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Patient", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Patient patient = new Patient();
            patient.setPatientID(cursor.getInt(0));
            patient.setPatientReference(cursor.getString(1));
            patientList.add(patient);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return patientList;
    }

    /**
     * Search for patients by patient reference from the database
     * @param reference The patient reference to search by
     * @return All patients with the specified reference
     * @throws SQLiteException
     */
    public List<Patient> searchPatients(String reference) throws SQLiteException{
        open();
        List<Patient> patientList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Patient WHERE patient_reference = ?",
                new String[]{reference});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Patient patient = new Patient();
            patient.setPatientID(cursor.getInt(0));
            patient.setPatientReference(cursor.getString(1));
            patientList.add(patient);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return patientList;
    }

    /**
     * Updates a patient's reference in the database
     * @param patient The updated patient
     * @throws SQLiteException
     */
    public void updatePatient(Patient patient) throws SQLiteException{
        open();
        ContentValues values = new ContentValues();
        values.put("patient_reference", patient.getPatientReference());
        db.update("Patient", values, "patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        close();
    }

    /**
     * Deletes a patient from the database
     * @param patient The patient to be deleted
     * @throws SQLiteException
     */
    public void deletePatient(Patient patient) throws SQLiteException{
        open();
        db.delete("Patient", "Patient_ID = " + patient.getPatientID(),
                null);
        close();
    }

    /**
     * Deletes all patients from the database, for testing purposes
     * @throws SQLException
     */
    public void deleteAllPatients() throws SQLException {
        open();
        db.delete("Patient", null, null);
        close();
    }

    //---------------------------------Game Methods-----------------------------------//

    /**
     * Returns all games from the database
     * @return A list of games
     * @throws SQLiteException
     */
    public List<Game> getGames() throws SQLiteException{
        open();
        List<Game> gameList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Game", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Game game = new Game();
            game.setGameID(cursor.getInt(0));
            game.setGameName(cursor.getString(1));
            game.setGameDesc(cursor.getString(2));
            gameList.add(game);
            cursor.moveToNext();
        }
        cursor.close();
        open();
        return gameList;
    }

    //---------------------------GameSession Methods---------------------------------//

    /**
     * Inserts a game session into the database
     * @param session The game session to be inserted
     * @throws SQLiteException
     */
    public void createSession(GameSession session) throws SQLiteException{
        open();
        ContentValues values = new ContentValues();
        values.put("patient_ID", session.getPatientID());
        values.put("game_ID", session.getGameID());
        values.put("metrics", session.getMetrics());
        values.put("date", session.getDate());
        db.insertOrThrow("Game_Session", null, values);
        close();
    }

    /**
     * Returns game sessions for a particular patient and game
     * @param patient The patient to retrieve the session for
     * @param game The game to retrieve the session for
     * @return List of game sessions
     * @throws SQLiteException
     */
    public List<GameSession> getSessions(Patient patient, Game game) throws SQLiteException{
        open();
        List<GameSession> sessionList = new ArrayList<>();
        String query = "SELECT * FROM Game_Session WHERE patient_ID = " + patient.getPatientID() +
                " AND game_ID = " + game.getGameID();
        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            GameSession session = new GameSession();
            session.setSessionID(cursor.getInt(0));
            session.setPatientID(cursor.getInt(1));
            session.setGameID(cursor.getInt(2));
            session.setMetrics(cursor.getDouble(3));
            session.setDate(cursor.getString(4));
            sessionList.add(session);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return sessionList;
    }

    //-----------------------------GameAssignment Methods-----------------------------//

    /**
     * Inserts a game assignment for a particular patient into the database
     * @param assignment The game assignment to be inserted
     * @throws SQLiteException
     */
    public void createAssignment(GameAssignment assignment) throws SQLiteException{
        open();
        ContentValues values = new ContentValues();
        values.put("game_ID", assignment.getGameID());
        values.put("patient_ID", assignment.getPatientID());
        values.put("num_of_attempts", assignment.getGameAttempts());
        db.insert("Game_Assignment", null, values);
        close();
    }

    /**
     * Returns all game assignments for a particular patient from the database
     * @param patient The patient to retrieve game assignments for
     * @return List of game assignments
     * @throws SQLiteException
     */
    public List<GameAssignment> getAssignments(Patient patient)throws SQLiteException{
        open();
        List<GameAssignment> gameAssignments = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Game_Assignment WHERE patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            GameAssignment assignment = new GameAssignment();
            assignment.setGameID(cursor.getInt(0));
            assignment.setPatientID(cursor.getInt(1));
            assignment.setGameAttempts(cursor.getInt(2));
            gameAssignments.add(assignment);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return gameAssignments;
    }

    /**
     * Updates a game assignment for a particular patient
     * @param assignment The assignment to be updated
     * @throws SQLiteException
     */
    public void updateAssignment(GameAssignment assignment) throws SQLiteException{
        open();
        ContentValues values = new ContentValues();
        values.put("game_ID", assignment.getGameID());
        values.put("num_of_attempts", assignment.getGameAttempts());
        db.update("Game_Assignment", values, "patient_ID = ?",
                new String[]{Integer.toString(assignment.getPatientID())});
        close();
    }
}
