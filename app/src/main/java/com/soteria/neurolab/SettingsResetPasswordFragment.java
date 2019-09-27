package com.soteria.neurolab;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class SettingsResetPasswordFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public SettingsResetPasswordFragment() {

    }

    public static SettingsResetPasswordFragment newInstance() {
        return new SettingsResetPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View passwordView =  inflater.inflate(R.layout.settings_reset_fragment, container, false);
        return passwordView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsResetPasswordFragment.OnFragmentInteractionListener) {
            mListener = (SettingsResetPasswordFragment.OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
