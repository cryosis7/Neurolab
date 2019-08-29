package com.soteria.neurolab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchPatientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchPatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchPatientFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView searchRecycler;


    public SearchPatientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchPatientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPatientFragment newInstance(String param1, String param2) {
        SearchPatientFragment fragment = new SearchPatientFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_search_patient, container, false);

        //Initialise UI elements
        final SearchView inputPatientID = view.findViewById(R.id.search_patient_SearchView);
        final ListView patientListView = view.findViewById(R.id.search_patient_ListView);


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_patient, container, false);

        searchRecycler = view.findViewById(R.id.searchPatient_searchRecycler);

        ArrayList<String> patientData = new ArrayList<>();
        patientData.add("SC87");
        patientData.add("JK90");
        patientData.add("BW97");
        patientData.add("RD92");

        searchRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        SearchPatientRecyclerAdapter adapter = new SearchPatientRecyclerAdapter(this.getActivity(), patientData);
        searchRecycler.setAdapter(adapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
}
