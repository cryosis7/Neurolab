package com.soteria.neurolab;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.models.GameAssignment;
import com.soteria.neurolab.models.Patient;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Jason Krieg
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreatePatientFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreatePatientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePatientFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public CreatePatientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreatePatientFragment.
     */
    public static CreatePatientFragment newInstance() {
        CreatePatientFragment fragment = new CreatePatientFragment();
        return fragment;
    }


    /**
     * Factory method for creating default fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* //TODO: What's this for? can we remove?
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        */
    }

    /**
     * Initializes UI elements including buttons and onClickListeners of the fragment
     * @param inflater - The fragment to be inflated
     * @param container - Default argument from factory method
     * @param savedInstanceState - Default argument from factory method
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_create_patient, container, false);

        //Initializing the UI elements
        final TextInputEditText editPatientID = view.findViewById(R.id.inputPatientID);
        final TextInputLayout editPatientIDLayout = view.findViewById(R.id.inputPatientIDLayout);
        final CheckBox checkBoxReaction = view.findViewById(R.id.checkReactionTime);
        final CheckBox checkBoxMotor = view.findViewById(R.id.checkMotorSkills);
        final CheckBox checkBoxAttention = view.findViewById(R.id.checkSelectiveAttention);
        final CheckBox checkBoxMemory = view.findViewById(R.id.checkVisualShortTermMemory);
        final View snackbarView = view.findViewById(R.id.snackbarCoordinator);

        //Initializing attempts seekbar and setting the on change listener
        final TextView seekAttemptsCount = view.findViewById(R.id.textAttemptsNumber);
        final SeekBar seekAttemptsBar = view.findViewById(R.id.seekAttempts);
        seekAttemptsCount.setText(Integer.toString(seekAttemptsBar.getProgress() + 1));
        seekAttemptsBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             * Changes the number displaying the attempts next to the seekbar
             * @param seekBar - The seekbar object
             * @param progress - The progress of the seekbar
             * @param fromUser - Changed by user (true) or system (false)
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekAttemptsCount.setText(Integer.toString(progress + 1));
            }
        });

        /**
         * onClickListener for the Create button.
         * Will read all the inputs and create a patient in the database and produce a
         * snackbar message for quick access to the newly created patient.
         */
        final Button createPatientButton = view.findViewById(R.id.buttonCreate);
        createPatientButton.setOnClickListener(new View.OnClickListener(){

            /**
             * The method for clicking the create patient button.
             * Checks that the patientID is not blank and is alphanumeric.
             * Creates a snackbar message to display that the patient was created and can be used to
             * open the patient page.
             */
            @Override
            public void onClick(View view){
                //Checking that patientID is alphanumerical
                String patientIDRegex = getString(R.string.alpha_numeric_regex);
                Pattern pattern = Pattern.compile(patientIDRegex);
                Matcher matcher = pattern.matcher(editPatientID.getText().toString());

                if(TextUtils.isEmpty(editPatientID.getText().toString())) {
                    editPatientIDLayout.setErrorEnabled(true);
                    editPatientIDLayout.setError(getString(R.string.error_patient_id_blank));
                } else if(!matcher.matches()) {
                    editPatientIDLayout.setErrorEnabled(true);
                    editPatientIDLayout.setError(getString(R.string.error_patient_id_special_characters));
                } else if( editPatientID.getText().toString().length() > 10 ) {
                    editPatientIDLayout.setErrorEnabled(true);
                    editPatientIDLayout.setError(getString(R.string.error_patient_id_length));
                } else {
                    final String patientReference = editPatientID.getText().toString().toUpperCase();
                    DatabaseAccess db = DatabaseAccess.getInstance(getContext());

                    if( patientNameCheck(db.getAllPatients(), patientReference) ) {
                        db.createPatient(patientReference);
                        Patient newPatient = db.getPatient(patientReference);
                        int attempts = seekAttemptsBar.getProgress() + 1;

                        if(checkBoxReaction.isChecked())
                            db.createAssignment(new GameAssignment(1, newPatient.getPatientID(), attempts ));
                        if(checkBoxMemory.isChecked())
                            db.createAssignment(new GameAssignment(2, newPatient.getPatientID(), attempts ));
                        if(checkBoxMotor.isChecked())
                            db.createAssignment(new GameAssignment(3, newPatient.getPatientID(), attempts ));
                        if(checkBoxAttention.isChecked())
                            db.createAssignment(new GameAssignment(4, newPatient.getPatientID(), attempts ));

                        Snackbar patientCreatedSnackbar = Snackbar.make(snackbarView, "Patient " + patientReference + " created", Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.open), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent viewIntent = new Intent(getActivity(), ViewPatientDetails.class);
                                        viewIntent.putExtra("PATIENT_REFERENCE", patientReference);
                                        startActivity(viewIntent);
                                    }
                                });
                        TextView snackbarTextView = patientCreatedSnackbar.getView().findViewById(R.id.snackbar_text);
                        TextView snackbarActionTextView = patientCreatedSnackbar.getView().findViewById(R.id.snackbar_action);
                        snackbarTextView.setTextSize(28);
                        snackbarActionTextView.setTextSize(28);
                        patientCreatedSnackbar.show();

                        editPatientID.setText("");
                        checkBoxAttention.setChecked(false);
                        checkBoxMemory.setChecked(false);
                        checkBoxMotor.setChecked(false);
                        checkBoxReaction.setChecked(false);
                        seekAttemptsBar.setProgress(2);
                        seekAttemptsCount.setText("3");
                    } else {
                        editPatientIDLayout.setErrorEnabled(true);
                        editPatientIDLayout.setError(getString(R.string.error_patient_id_in_use));
                    }
                }
            }
        });

        /**
         * Listener on the edit text field to clear the error upon changing the patientID
         */
        editPatientID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editPatientIDLayout.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        return view;
    }

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
        void onFragmentInteraction(Uri uri);
    }

    private boolean patientNameCheck( List<Patient> databasePatients, String newID )
    {
        for( Patient temp : databasePatients )
        {
            if( temp.getPatientReference().equals(newID))
                return false;
        }
        return true;
    }
}
