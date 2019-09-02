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

        Patient patient = new Patient("BW24");

        db.createPatient(patient);
        List<Patient> patients = db.getAllPatients();

        assertThat(patients.size(), is(1));
        assertEquals(patients.get(0).getPatientReference(), "BW24");
    }

    @Test
    public void testSelectAllPatients(){
        db.deleteAllPatients();

        Patient patient1 = new Patient("BW01");
        Patient patient2 = new Patient("MT02");

        db.createPatient(patient1);
        db.createPatient(patient2);

        assertThat(db.getAllPatients().size(), is(2));
    }

    @Test
    public void testDeleteAllAndOnePatient() {
        db.deleteAllPatients();
        assertThat(db.getAllPatients().size(), is(0));

        Patient patient = new Patient("RD26");
        db.createPatient(patient);
        List<Patient> patients = db.getAllPatients();

        assertThat(patients.size(), is(1));

        db.deletePatient(patients.get(0));
        assertThat(db.getAllPatients().size(), is(0));
    }

   @Test
    public void testAddDeleteMultiple(){
       db.deleteAllPatients();
        Patient patient1 = new Patient("JK10");
        Patient patient2 = new Patient("SC12");
        Patient patient3 = new Patient("MT15");

        db.createPatient(patient1);
        db.createPatient(patient2);
        db.createPatient(patient3);

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
        Patient patient1 = new Patient("BW05");
        Patient patient2 = new Patient("BW05");
        db.createPatient(patient1);
        db.createPatient(patient2);

        List<Patient> patients = db.searchPatients("BW05");
        assertEquals(patients.size(), 2);
        assertEquals(patients.get(0).getPatientReference(), "BW05");
    }

    @Test
    public void testUpdatePatient(){
        db.deleteAllPatients();
        Patient patient = new Patient("BW50");
        db.createPatient(patient);

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
        Patient p = new Patient("NP20");
        db.createPatient(p);
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
        Patient p = new Patient("NP20");
        db.createPatient(p);
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
