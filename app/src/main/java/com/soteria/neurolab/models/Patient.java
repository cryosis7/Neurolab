package com.soteria.neurolab.models;

public class Patient {
    private int patientID;
    private String patientReference;

    public Patient(String patientReference) {
        this.patientReference = patientReference;
    }

    public Patient(){}

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public String getPatientReference() {
        return patientReference;
    }

    public void setPatientReference(String patientReference) {
        this.patientReference = patientReference;
    }
}
