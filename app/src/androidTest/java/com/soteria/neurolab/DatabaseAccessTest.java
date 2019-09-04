package com.soteria.neurolab;



import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.soteria.neurolab.models.Game;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.GameSession;
import com.soteria.neurolab.models.Patient;
import com.soteria.neurolab.database.DatabaseAccess;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
/*
@RunWith(AndroidJUnit4.class)
public class DatabaseAccessTest extends Assert {
    private DatabaseAccess db;

    @Before
    public void setUp() {
        db = new DatabaseAccess(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void testPreConditions(){
        assertNotNull(db);
    }

    //-------------------------Patient Tests------------------------------

    @Test
    public void testAddPatient() {
        db.deleteAllPatients();

        db.createPatient("BW24");
        List<Patient> patients = db.getAllPatients();

        assertThat(patients.size(), is(1));
        assertEquals(patients.get(0).getPatientReference(), "BW24");
    }

    @Test
    public void testSelectAllPatients(){
        db.deleteAllPatients();

        db.createPatient("BW01");
        db.createPatient("MT02");

        assertThat(db.getAllPatients().size(), is(2));
    }

    @Test
    public void testDeleteAllAndOnePatient() {
        db.deleteAllPatients();
        assertThat(db.getAllPatients().size(), is(0));

        db.createPatient("RD26");
        List<Patient> patients = db.getAllPatients();

        assertThat(patients.size(), is(1));

        db.deletePatient(patients.get(0));
        assertThat(db.getAllPatients().size(), is(0));
    }

   @Test
    public void testAddDeleteMultiple(){
       db.deleteAllPatients();

        db.createPatient("JK10");
        db.createPatient("SC12");
        db.createPatient("MT15");

        List<Patient> patients = db.getAllPatients();
        assertThat(patients.size(), is(3));

        db.deletePatient(patients.get(1));
        db.deletePatient(patients.get(2));

        patients = db.getAllPatients();
        assertThat(patients.size(), is(1));
    }

    @Test
    public void testSelectPatient(){
        db.deleteAllPatients();
        Patient patient2 = new Patient("BW05");
        db.createPatient("BW05");

        List<Patient> patients = db.searchPatients("BW05");
        assertEquals(patients.size(), 1);
        assertEquals(patients.get(0).getPatientReference(), "BW05");
    }

    @Test
    public void testUpdatePatient(){
        db.deleteAllPatients();
        db.createPatient("BW05");

        List<Patient> patients = db.getAllPatients();
        Patient p = patients.get(0);
        assertEquals(patients.size(),1);
        assertEquals(p.getPatientReference(),"BW50");

        p.setPatientReference("BW70");
        db.updatePatient(p);
        List<Patient> updatedPatients = db.getAllPatients();
        Patient updatedPatient = updatedPatients.get(0);

        assertEquals(updatedPatient.getPatientReference(),"BW70");
    }

    //-------------------------Game Tests------------------------------

    @Test
    public void testGetAllGames(){
        List<Game> games = db.getGames();
        assertThat(games.size(), is(4));

        String reactionGame = games.get(0).getGameName();
        String motorSkills = games.get(2).getGameName();

        assertEquals(reactionGame,"Reaction Time");
        assertEquals(motorSkills,"Motor Skills");
    }

    //-------------------------Assignment Tests------------------------------

    @Test
    public void testCreateAndGetAssignment(){
        db.createPatient("NP20");
        List<Patient> patients = db.getAllPatients();
        Patient patient = patients.get(0);

        List<Game> games = db.getGames();
        Game reactionGame = games.get(0);

        GameAssignment assignment = new GameAssignment(reactionGame.getGameID(),
                patient.getPatientID(), 5 );
        db.createAssignment(assignment);
        List<GameAssignment> assignments = db.getAssignments(patient);
        GameAssignment patientAssignment = assignments.get(0);

        assertThat(assignments.size(), is(1));
        assertEquals(patientAssignment.getPatientID(), patient.getPatientID());
        assertEquals(patientAssignment.getGameID(),reactionGame.getGameID());
    }

    @Test
    public void testUpdateAssignment(){
        List<Patient> patients = db.getAllPatients();
        Patient patient = patients.get(0);

        List<Game> games = db.getGames();
        Game reactionGame = games.get(0);

        GameAssignment assignment = new GameAssignment(reactionGame.getGameID(),
                patient.getPatientID(), 3);

        db.updateAssignment(assignment);
        List<GameAssignment> assignments = db.getAssignments(patient);
        GameAssignment updatedAssignment = assignments.get(0);

        assertThat(assignments.size(), is(1));
        assertEquals(updatedAssignment.getGameAttempts(),3);
    }


//-------------------------Session Tests------------------------------

    @Test
    public void testCreateAndGetSession(){
        db.createPatient("NP20");
        List<Patient> patients = db.getAllPatients();
        Patient patient = patients.get(0);

        List<Game> games = db.getGames();
        Game game = games.get(0);

        GameSession session = new GameSession(patient.getPatientID(), game.getGameID(),
                79.5, "2019-08-28");
        db.createSession(session);

        List<GameSession> sessions = db.getAllSessions(patient, game);
        GameSession gameSession = sessions.get(0);

        assertThat(sessions.size(), is(1));
        assertEquals(gameSession.getGameID(), session.getGameID());
        assertEquals(gameSession.getPatientID(), session.getPatientID());
    }
}
*/

