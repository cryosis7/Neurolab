package com.soteria.neurolab.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.soteria.neurolab.models.Game;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.GameSession;
import com.soteria.neurolab.models.Patient;
import com.soteria.neurolab.utilities.DateManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    private Cursor cursor = null;

    public DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) throws SQLiteException {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Opens the database
     *
     * @throws SQLiteException
     */
    public void open() throws SQLiteException {
        try {
            this.db = openHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the database
     */
    public void close() {
        if (db != null) {
            this.db.close();
        }
    }

    //--------------------------------Patient Methods--------------------------------------//

    /**
     * Inserts a patient object into the database
     *
     * @param patientReference The patient reference to be inserted
     * @throws SQLiteException
     */
    public void createPatient(String patientReference) throws SQLiteException {
        open();
        ContentValues values = new ContentValues();
        values.put("patient_reference", patientReference);
        long result = db.insert("Patient", null, values);
        if (result == -1)
            throw new SQLException("Error occurred while inserting " + patientReference + " into the database");
        close();
    }

    /**
     * Retrieves a list of all patients from the database
     *
     * @return a list of patients
     * @throws SQLiteException
     */
    public List<Patient> getAllPatients() throws SQLiteException {
        open();
        List<Patient> patientList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Patient", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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

    public List<String> getAllPatientReferences() throws SQLiteException {
        open();
        List<String> patientList = new ArrayList<>();
        cursor = db.rawQuery("SELECT patient_reference FROM Patient", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String ref = cursor.getString(0);
            patientList.add(ref);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return patientList;
    }


    /**
     * Search for patients by patient reference from the database
     *
     * @param reference The patient reference to search by
     * @return All patients with the specified reference
     * @throws SQLiteException
     */
    public List<Patient> searchPatients(String reference) throws SQLiteException {
        open();
        List<Patient> patientList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Patient WHERE patient_reference = ?", //TODO: Should this not be patient_reference LIKE ?
                new String[]{reference});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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
     * Search for a specific patient by patientID
     *
     * @param patientID Internal database patient identifier (Not patient reference)
     * @return The patient with the specified ID
     * @throws SQLiteException
     */
    public Patient getPatient(int patientID) throws SQLiteException {
        Patient patient = new Patient();
        open();
        cursor = db.rawQuery("SELECT * FROM Patient WHERE patient_id = ?",
                new String[]{Integer.toString(patientID)});
        if (cursor.getCount() > 1) {
            cursor.close();
            close();
            throw new SQLiteException("Multiple patients match patientID: " + patientID);
        } else if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        } else {
            cursor.moveToFirst();
            patient.setPatientID(cursor.getInt(0));
            patient.setPatientReference(cursor.getString(1));
            cursor.close();
            close();
            return patient;
        }
    }

    /**
     * Search for a specific patient by patientID
     *
     * @param patientReference - Internal database patient identifier (Not patient reference)
     * @return - The patient with the specified ID or Null
     * @throws SQLiteException
     */
    public Patient getPatient(String patientReference) throws SQLiteException {
        Patient patient = new Patient();
        open();
        cursor = db.rawQuery("SELECT * FROM Patient WHERE patient_reference = ?",
                new String[]{patientReference});

        if (cursor.getCount() > 1) {
            cursor.close();
            close();
            throw new SQLiteException("Multiple patients match patient_reference: " + patientReference);
        } else if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        } else {
            cursor.moveToFirst();
            patient.setPatientID(cursor.getInt(0));
            patient.setPatientReference(cursor.getString(1));
            cursor.close();
            close();
            return patient;
        }
    }

    /**
     * Updates a patient's reference in the database
     *
     * @param patient The updated patient
     * @throws SQLiteException
     */
    public void updatePatient(Patient patient) throws SQLiteException {
        open();
        ContentValues values = new ContentValues();
        values.put("patient_reference", patient.getPatientReference());
        db.update("Patient", values, "patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        close();
    }

    /**
     * Deletes a patient from all tables in the database
     *
     * @param patient The patient to be deleted
     * @throws SQLiteException
     */
    public void deletePatient(Patient patient) throws SQLiteException {
        open();
        db.delete("Patient", "Patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        db.delete("Game_Assignment", "Patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        db.delete("Game_Session", "Patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        close();
    }


    /**
     * Function for cleaning the Patient table, used for testing purposes only.
     *
     * @throws SQLiteException
     */
    public void deleteAllPatients() throws SQLiteException {
        open();
        db.execSQL("delete from Patient");
        close();
    }

    //---------------------------------Game Methods-----------------------------------//

    /**
     * Returns all games from the database
     *
     * @return A list of games
     * @throws SQLiteException
     */
    public List<Game> getGames() throws SQLiteException {
        open();
        List<Game> gameList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Game", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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

    /**
     * Gets a game object by it's id
     *
     * @param gameID
     * @return
     * @throws SQLiteException
     */
    public Game getGame(String gameID) throws SQLiteException {
        open();
        Game game = new Game();
        cursor = db.rawQuery("SELECT * FROM Game WHERE game_ID = ?", new String[]{gameID});

        if (cursor.getCount() > 1) {
            cursor.close();
            close();
            throw new SQLiteException("Multiple games match the gameID: " + gameID);
        } else if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        } else {
            cursor.moveToFirst();
            game.setGameID(cursor.getInt(0));
            game.setGameName(cursor.getString(1));
            game.setGameDesc(cursor.getString(2));

            cursor.close();
            close();
            return game;
        }
    }

    /**
     * Retrieves a Game object using it's name as an identifier.
     *
     * @param gameName A string value of the game name.
     * @return a Game object
     * @throws SQLiteException
     */
    public Game getGameByName(String gameName) throws SQLiteException {
        open();
        Game game = new Game();
        cursor = db.rawQuery("SELECT * FROM Game WHERE game_name = ?", new String[]{gameName});

        if (cursor.getCount() > 1) {
            cursor.close();
            close();
            throw new SQLiteException("Multiple games match the gameName: " + gameName);
        } else if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        } else {
            cursor.moveToFirst();
            game.setGameID(cursor.getInt(0));
            game.setGameName(cursor.getString(1));
            game.setGameDesc(cursor.getString(2));

            cursor.close();
            close();
            return game;
        }
    }

    /**
     * Retrieves a Game ID using it's name as an identifier.
     *
     * @param gameName The game name to search for - case-sensitive.
     * @return an int representing the game-id.
     * @throws SQLiteException
     */
    public int getGameId(String gameName) throws SQLiteException {
        open();
        cursor = db.rawQuery("SELECT game_ID FROM Game WHERE game_name = ?", new String[]{gameName});

        if (cursor.getCount() > 1) {
            cursor.close();
            close();
            throw new SQLiteException("Multiple games match the game name: " + gameName);
        } else if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return -1;
        } else {
            int id;
            cursor.moveToFirst();
            id = cursor.getInt(0);

            cursor.close();
            close();
            return id;
        }
    }

    //---------------------------GameSession Methods---------------------------------//

    /**
     * Inserts a game session into the database
     *
     * @param session The game session to be inserted
     * @throws SQLiteException
     */
    public void createSession(GameSession session) throws SQLiteException {
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
     *
     * @param patient The patient to retrieve the session for
     * @param game    The game to retrieve the session for
     * @return List of game sessions
     * @throws SQLiteException
     */
    public List<GameSession> getAllSessions(Patient patient, Game game) throws SQLiteException {
        open();
        cursor = db.rawQuery(
                "SELECT * FROM Game_Session WHERE patient_ID = ? AND game_ID = ?",
                new String[]{String.valueOf(patient.getPatientID()), String.valueOf(game.getGameID())});
        return getSessionList(cursor);
    }

    /**
     * Returns all game sessions for a particular patient and game
     *
     * @param patientID The patient to retrieve the session for
     * @param gameID    The game to retrieve the session for
     * @return List of game sessions
     * @throws SQLiteException
     */
    public List<GameSession> getAllSessions(String patientID, String gameID) throws SQLiteException {
        open();
        cursor = db.rawQuery(
                "SELECT * FROM Game_Session WHERE patient_ID = ? AND game_ID = ?",
                new String[]{patientID, gameID});
        return getSessionList(cursor);
    }

    /**
     * Returns the game sessions for a particular patient and game in the last month
     *
     * @param patientID The patient to retrieve the session for
     * @param gameID    The game to retrieve the session for
     * @return List of game sessions
     * @throws SQLiteException
     */
    public List<GameSession> getLastMonthSessions(String patientID, String gameID) throws SQLiteException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String dateString = DateManager.getDateString(cal.getTime());

        open();
        cursor = db.rawQuery(
                "SELECT * FROM Game_Session WHERE patient_ID = ? AND game_ID = ? AND date >= ? AND date <= ?",
                new String[]{patientID, gameID, dateString, DateManager.getDateString(new Date())});
        return getSessionList(cursor);
    }

    /**
     * Returns the game sessions for a particular patient and game in the last week
     *
     * @param patientID The patient to retrieve the session for
     * @param gameID    The game to retrieve the session for
     * @return List of game sessions
     * @throws SQLiteException
     */
    public List<GameSession> getLastWeekSessions(String patientID, String gameID) throws SQLiteException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        String dateString = DateManager.getDateString(cal.getTime());

        open();
        cursor = db.rawQuery(
                "SELECT * FROM Game_Session WHERE patient_ID = ? AND game_ID = ? AND date >= ? AND date <= ?",
                new String[]{patientID, gameID, dateString, DateManager.getDateString(new Date())});
        return getSessionList(cursor);
    }

    /**
     * Returns the game sessions for a particular patient and game that were attempted today.
     *
     * @param patientID The patient to retrieve the session for
     * @param gameID    The game to retrieve the session for
     * @return List of GameSessions
     * @throws SQLiteException
     */
    public List<GameSession> getTodaysSessions(int patientID, int gameID) throws SQLiteException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String dateString = DateManager.getDateString(cal.getTime());

        open();
        cursor = db.rawQuery(
                "SELECT * FROM Game_Session WHERE patient_ID = ? AND game_ID = ? AND date >= ? AND date <= ?",
                new String[]{String.valueOf(patientID), String.valueOf(gameID), dateString, DateManager.getDateString(new Date())});
        return getSessionList(cursor);
    }

    /**
     * Converts a cursor into a list of game sessions
     *
     * @param cursor The cursor containing a query of GameSessions
     * @return a List of GameSession objects.
     */
    private List<GameSession> getSessionList(Cursor cursor) {
        List<GameSession> sessionList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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

    /**
     * Updates all values for the session matching the sessionID
     *
     * @param session
     */
    public void updateSession(GameSession session) {
        open();
        ContentValues values = new ContentValues();
        values.put("patient_ID", session.getPatientID());
        values.put("game_ID", session.getGameID());
        values.put("metrics", session.getMetrics());
        values.put("date", session.getDate());

        db.update("Game_Session", values, "session_ID=?",
                new String[]{Integer.toString(session.getPatientID())});
        close();
    }

    /**
     * Deletes all sessions in the database matching the patientID and gameID.
     *
     * @param patientID
     * @param gameID
     */
    public void deleteAllSessions(String patientID, String gameID) {
        open();
        db.delete("Game_Session", "patient_ID=? AND game_ID=?", new String[]{patientID, gameID});
        close();
    }

    /**
     * Deletes all game sessions, used for testing purposes only
     *
     * @throws SQLiteException
     */
    public void deleteAllSessions() throws SQLiteException {
        open();
        db.execSQL("delete from Game_Session");
        close();
    }

    //-----------------------------GameAssignment Methods-----------------------------//

    /**
     * Inserts a game assignment for a particular patient into the database
     *
     * @param assignment The game assignment to be inserted
     * @throws SQLiteException
     */
    public void createAssignment(GameAssignment assignment) throws SQLiteException {
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
     *
     * @param patient The patient to retrieve game assignments for
     * @return List of game assignments
     * @throws SQLiteException
     */
    public List<GameAssignment> getAssignments(Patient patient) throws SQLiteException {
        open();
        List<GameAssignment> gameAssignments = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Game_Assignment WHERE patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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

    public boolean checkAssignments(Patient patient) throws SQLiteException {
        open();
        cursor = db.rawQuery("SELECT * FROM Game_Assignment WHERE patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return false;
        } else {
            cursor.close();
            close();
            return true;
        }
    }

    /**
     * Returns all game assignments for a particular patient from the database
     *
     * @param patientId The id of the patient to retrieve game assignments for
     * @return List of game assignments
     * @throws SQLiteException
     */
    public List<GameAssignment> getAssignments(int patientId) throws SQLiteException {
        open();
        List<GameAssignment> gameAssignments = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Game_Assignment WHERE patient_ID = ?",
                new String[]{String.valueOf(patientId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
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
     * Retrieves a Game Assignment object for a particular patient and game
     *
     * @param patientId
     * @param gameId
     * @return Object representing an assignment of a game to a patient containing a number of attempts allowed.
     * @throws SQLiteException
     */
    public GameAssignment getAssignment(int patientId, int gameId) throws SQLiteException {
        GameAssignment assignment = new GameAssignment();
        open();
        cursor = db.rawQuery("SELECT * FROM Game_Assignment WHERE patient_ID = ? AND game_ID = ?",
                new String[]{String.valueOf(patientId), String.valueOf(gameId)});

        if (cursor.getCount() > 1) {
            cursor.close();
            close();
            throw new SQLiteException("Multiple Game Assignments for patient_id: " + patientId + " game_id: " + gameId);
        } else if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return null;
        } else {
            cursor.moveToFirst();
            assignment.setGameID(cursor.getInt(0));
            assignment.setPatientID(cursor.getInt(1));
            assignment.setGameAttempts(cursor.getInt(2));
            cursor.close();
            close();

            return assignment;
        }
    }

    /**
     * Gets a list of the names of the games a patient is assigned to.
     *
     * @param patientId the ID of the patient to look up
     * @return A list of Strings representing each game they are assigned to.
     * @throws SQLiteException
     */
    public List<String> getAssignmentNames(int patientId) throws SQLiteException {
        open();
        List<String> gameAssignmentNames = new ArrayList<>();
        cursor = db.rawQuery("SELECT game_name FROM Game WHERE game_ID IN" +
                        "(SELECT Game_Assignment.game_ID FROM Game_Assignment WHERE patient_ID = ?)",
                new String[]{String.valueOf(patientId)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            gameAssignmentNames.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return gameAssignmentNames;
    }

    /**
     * Updates the number of attempts in a game assignment
     *
     * @param assignment The assignment to be updated
     * @throws SQLiteException
     */
    public void updateAssignment(GameAssignment assignment) throws SQLiteException {
        open();
        ContentValues values = new ContentValues();
        values.put("num_of_attempts", assignment.getGameAttempts());
        db.update("Game_Assignment", values, "patient_ID = ? AND game_ID = ?",
                new String[]{
                        Integer.toString(assignment.getPatientID()),
                        Integer.toString(assignment.getGameID())
                });
        close();
    }

    /**
     * Deletes a game assignment from the database.
     *
     * @param assignment The assignment to be updated.
     * @throws SQLiteException
     */
    public void deleteAssignment(GameAssignment assignment) throws SQLiteException {
        open();
        db.delete("Game_Assignment", "patient_ID = ? AND game_ID = ?",
                new String[]{
                        Integer.toString(assignment.getPatientID()),
                        Integer.toString(assignment.getGameID())
                });
        close();
    }


    /**
     * Deletes all game assignments, used for testing purposes only
     *
     * @throws SQLiteException
     */
    public void deleteAllAssignments() throws SQLiteException {
        open();
        db.execSQL("delete from Game_Assignment");
        close();
    }

    //---------------------------Other Methods---------------------------------//


    public String getLatestDate(int patientID) {
        String latestDate;

        open();
        cursor = db.rawQuery(
                "SELECT * FROM Game_Session WHERE patient_ID = ? ORDER BY date DESC",
                new String[]{String.valueOf(patientID)});

        if (cursor.getCount() == 0) {
            cursor.close();
            close();
            return "No games have been played on this account";
        } else {
            cursor.moveToFirst();
            latestDate = (cursor.getString(4));
            cursor.close();
            close();
            return latestDate;
        }
    }
}
