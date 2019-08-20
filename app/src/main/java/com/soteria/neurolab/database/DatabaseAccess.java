package com.soteria.neurolab.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    //Private Constructor
    private DatabaseAccess(Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }

    //Return single instance of databases
    public static DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    //Open databases
    public  void open(){
        this.db = openHelper.getWritableDatabase();
    }

    //Close database
    public void close(){
        if(db != null){
            this.db.close();
        }
    }

    //--------------------------------Patient Methods--------------------------------------//

    //Create Patient
    public void createPatient(Patient patient){
        open();
        ContentValues values = new ContentValues();
        values.put("patient_reference", patient.getPatientReference());
        db.insert("Patient", null, values);
        close();
    }

    //Get List of all patients
    public List<Patient> getAllPatients(){
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

    //Get patients by reference
    public List<Patient> searchPatients(String reference){
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

    //Update Patient Reference
    public void updatePatient(Patient patient){
        open();
        ContentValues values = new ContentValues();
        values.put("patient_reference", patient.getPatientReference());
        db.update("Patient", values, "patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        close();
    }

    //Delete Patient
    public void deletePatient(Patient patient){
        open();
        db.delete("Patient", "patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        close();
    }

    //---------------------------------Game Methods-----------------------------------//

    //Get list of games
    public List<Game> getGames(){
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
        close();
        return gameList;
    }

    //---------------------------GameSession Methods---------------------------------//

    //Insert game session into database
    public void createSession(GameSession session){
        open();
        ContentValues values = new ContentValues();
        values.put("patient_ID", session.getPatientID());
        values.put("game_ID", session.getGameID());
        values.put("metrics", session.getMetrics());
        values.put("date", session.getDate());
        db.insert("Game_Session", null, values);
        close();
    }

    //Get list of game sessions for specific patient
    public List<GameSession> getSessions(Patient patient, Game game){
        open();
        List<GameSession> sessionList = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Game_Session WHERE patient_ID = ? AND game_ID = ?",
                new String[]{Integer.toString(patient.getPatientID(), game.getGameID())});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            GameSession session = new GameSession();
            session.setSessionID(cursor.getInt(0));
            session.setPatientID(cursor.getInt(1));
            session.setGameID(cursor.getInt(2));
            session.setMetrics(cursor.getFloat(3));
            session.setDate(cursor.getString(4));
            sessionList.add(session);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return sessionList;
    }

    //-----------------------------GameAssignment Methods-----------------------------//

    //Create game assignment
    public void createAssignment(GameAssignment assignment){
        open();
        ContentValues values = new ContentValues();
        values.put("game_ID", assignment.getGameID());
        values.put("patient_ID", assignment.getPatientID());
        values.put("num_of_attempts", assignment.getGameAttempts());
        db.insert("Game_Assignment", null, values);
        close();
    }

    //Get game assignments for specific patient
    public List<GameAssignment> getAssignments(Patient patient){
        open();
        List<GameAssignment> gameAssignments = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM Game_Assignment WHERE patient_ID = ?",
                new String[]{Integer.toString(patient.getPatientID())});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            GameAssignment assignment = new GameAssignment();
            assignment.setGameID(cursor.getInt(0));
            assignment.setPatientID(cursor.getInt(1));
            assignment.setGameAttempts(cursor.getInt(3));
            gameAssignments.add(assignment);
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return gameAssignments;
    }

   //Update game assignments for specific patient
    public void updateAssignments(GameAssignment assignment){
        open();
        ContentValues values = new ContentValues();
        values.put("game_ID", assignment.getGameID());
        values.put("num_of_attempts", assignment.getGameAttempts());
        db.update("Game_Assignment", values, "patient_ID = ?",
                new String[]{Integer.toString(assignment.getPatientID())});
        close();
    }
}
