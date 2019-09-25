package com.soteria.neurolab;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.Patient;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchPatientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchPatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchPatientFragment extends Fragment implements SearchPatientRecyclerAdapter.ItemClickListener,
        SearchView.OnQueryTextListener {

    private OnFragmentInteractionListener mListener;
    private SearchPatientRecyclerAdapter adapter;

    public SearchPatientFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchPatientFragment.
     */
    // TODO: Rename and change types and number of parameters
    private static SearchPatientFragment newInstance(String param1, String param2) {
        return new SearchPatientFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_search_patient, container, false);
        final SearchView searchInput = view.findViewById(R.id.searchPatient_searchInput);
        final RecyclerView searchRecycler = view.findViewById(R.id.searchPatient_searchRecycler);
        searchInput.setOnQueryTextListener(this);

        //Set the recycler view layout to set the information and the click listener
        searchRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        adapter = new SearchPatientRecyclerAdapter(this.getActivity(), getPatientList());
        adapter.setClickListener(this);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        DividerItemDecoration divider = new DividerItemDecoration(searchRecycler.getContext(), layout.getOrientation());
        searchRecycler.addItemDecoration(divider);
        searchRecycler.setAdapter(adapter);
        return view;
    }


    //Opens ViewPatientDetails page when a patient is clicked
    //Added an if/else clause to check if the patient still exists, else it removes it from the list
    @Override
    public void onItemClick(View view, int position)
    {
        if(SearchCreateDeleteActivity.db.getPatient(adapter.getItem(position)) != null){
            Intent viewPatient = new Intent(getActivity(), ViewPatientDetails.class);
            viewPatient.putExtra("PATIENT_REFERENCE", adapter.getItem(position));
            startActivity(viewPatient);
        } else {
            adapter.updateList(getPatientList());
            Toast.makeText(getActivity(), getResources().getString(R.string.search_patient_error_toast_text),Toast.LENGTH_LONG).show();
        }
    }


    //Returns a list of all patients from the database
    private List<String> getPatientList(){
        DatabaseAccess db = DatabaseAccess.getInstance(getContext());
        return db.getAllPatientReferences();
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    //Filters patient list when user types in search box
    @Override
    public boolean onQueryTextChange(String s) {

        String userInput = s.toUpperCase();
        List<String> newList = new ArrayList<>();

        for(String patient : getPatientList()){
            if(patient.toUpperCase().contains(userInput.toUpperCase())){
                newList.add(patient);
            }
        }
        adapter.updateList(newList);
        return true;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
