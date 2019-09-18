package com.soteria.neurolab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeletePatientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeletePatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeletePatientFragment extends Fragment implements SearchPatientRecyclerAdapter.ItemClickListener,
        SearchView.OnQueryTextListener {

    private OnFragmentInteractionListener mListener;

    public DeletePatientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeletePatientFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeletePatientFragment newInstance() {
        DeletePatientFragment fragment = new DeletePatientFragment();
        return fragment;
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
        searchInput.setOnQueryTextListener(this);

        //Set the recycler view layout to set the information and the click listener
        searchRecycler.setLayoutManager(new LinearLayoutManager(this.getActivity()));


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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {

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
