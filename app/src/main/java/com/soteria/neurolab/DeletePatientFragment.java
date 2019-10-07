package com.soteria.neurolab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Patient;
import com.soteria.neurolab.viewModels.DeletePatientRecyclerItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeletePatientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeletePatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeletePatientFragment extends Fragment implements DeletePatientRecyclerAdapter.ItemClickListener,
        SearchView.OnQueryTextListener {

    private OnFragmentInteractionListener mListener;
    private DeletePatientRecyclerAdapter adapter;

    //Full list is all in the database, current list is filtered by the search field
    private ArrayList<DeletePatientRecyclerItem> DPRItemFullList;
    private ArrayList<DeletePatientRecyclerItem> DPRItemCurrentList;

    public DeletePatientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeletePatientFragment.
     */
    private static DeletePatientFragment newInstance() {
        return new DeletePatientFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_delete_patient, container, false);
        final SearchView searchInput = view.findViewById(R.id.deletePatient_searchInput);
        final RecyclerView searchRecycler = view.findViewById(R.id.deletePatient_searchRecycler);
        final CheckBox selectAllCheckBox = view.findViewById(R.id.deletePatient_selectAllCheckBox);
        final Button deletePatientsButton = view.findViewById(R.id.deletePatient_deleteButton);
        searchInput.setOnQueryTextListener(this);

        //Set the recycler view layout to set the information and the click listener
        searchRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        initializePatientList();
        adapter = new DeletePatientRecyclerAdapter(this.getActivity(), DPRItemCurrentList);
        adapter.setClickListener(this);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        DividerItemDecoration divider = new DividerItemDecoration(searchRecycler.getContext(), layout.getOrientation());
        searchRecycler.addItemDecoration(divider);
        searchRecycler.setAdapter(adapter);

        //Sets all of the items in the current list to selected or deselected
        selectAllCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for(DeletePatientRecyclerItem item : DPRItemCurrentList){
                    item.setIsSelected(selectAllCheckBox.isChecked());
                }
                adapter.updateList(DPRItemCurrentList);
            }
        });

        //Handler for the delete button
        deletePatientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //Create a list and add all selected items in the current list
            public void onClick(View view) {
                final ArrayList<DeletePatientRecyclerItem> markedForDeleteList = new ArrayList<>();
                for (DeletePatientRecyclerItem item : DPRItemCurrentList) {
                    if (item.getIsSelected()) {
                        markedForDeleteList.add(item);
                    }
                }
                //If there are more than 0 items selected then handle delete stuff
                if (markedForDeleteList.size() > 0) {
                    //Build the alert dialog warning the user of their action.
                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(getContext());
                    deleteBuilder.setTitle(getResources().getString(R.string.title_delete_patient));
                    if(markedForDeleteList.size() == 1) {
                        deleteBuilder.setMessage(getString(R.string.delete_patient_confirmation_dialog_1_selected));
                    } else {
                        deleteBuilder.setMessage(getString(R.string.delete_patient_confirmation_dialog, String.valueOf(markedForDeleteList.size())));
                    }
                    //If delete is pressed, delete the patient
                    deleteBuilder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseAccess dbDelete = DatabaseAccess.getInstance(getContext());
                            for (DeletePatientRecyclerItem item : markedForDeleteList) {
                                Patient patientToDelete = dbDelete.getPatient(item.getPatientReference());
                                dbDelete.deletePatient(patientToDelete);
                            }
                            dialogInterface.cancel();
                            initializePatientList();
                            searchInput.setQuery("", false);
                            searchInput.clearFocus();
                            selectAllCheckBox.setChecked(false);
                            adapter.updateList(DPRItemCurrentList);
                            startActivity(new Intent(getContext(), SearchCreateDeleteActivity.class));
                        }
                    });
                    //If cancel is pressed, close the alert dialog.
                    deleteBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    final AlertDialog deleteConfirm = deleteBuilder.create();
                    deleteConfirm.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0) {
                            deleteConfirm.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                            deleteConfirm.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorWarning));
                            deleteConfirm.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                            deleteConfirm.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });
                    deleteConfirm.show();
                    TextView bodyText = deleteConfirm.findViewById(android.R.id.message);
                    bodyText.setTextSize(24);
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.delete_patient_no_patients_selected),Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    //Calls the database and sets the full list of patients, also sets the current list to equal the full list
    private void initializePatientList() {
        DPRItemFullList = new ArrayList<>();
        DatabaseAccess db = DatabaseAccess.getInstance(getContext());
        List<String> patientList = db.getAllPatientReferences();
        for(String patient : patientList){
            DPRItemFullList.add(new DeletePatientRecyclerItem(patient));
        }
        DPRItemCurrentList = DPRItemFullList;
    }

    //Method for attaching the fragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    //Method for detaching the fragment
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Required by interface
    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    //updates the current list based on the search string
    @Override
    public boolean onQueryTextChange(String s) {
        String userInput = s.toUpperCase();
        ArrayList<DeletePatientRecyclerItem> newList = new ArrayList<>();

        for(DeletePatientRecyclerItem patient : DPRItemFullList){
            if(patient.getPatientReference().toUpperCase().contains(userInput)){
                newList.add(patient);
            }
        }
        DPRItemCurrentList = newList;
        adapter.updateList(newList);
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        //Required by implemented class, not used here
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        //Lol why exist?
    }
}
