package com.soteria.neurolab.viewModels;

public class DeletePatientRecyclerItem {

    private boolean isSelected;
    private String patientReference;

    public String getPatientReference() {
        return patientReference;
    }

    public void setPatientReference(String patientReference) {
        this.patientReference = patientReference;
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
