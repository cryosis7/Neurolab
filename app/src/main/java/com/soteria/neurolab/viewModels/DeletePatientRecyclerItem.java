package com.soteria.neurolab.viewModels;

/**
 * Each item in the delete patient page list is an object of this
 * Getters and setters and a constructor
 */
public class DeletePatientRecyclerItem {

    private boolean isSelected;
    private String patientReference;

    public String getPatientReference() {
        return patientReference;
    }

    public DeletePatientRecyclerItem(String patientReference) {
        this.isSelected = false;
        this.patientReference = patientReference;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
