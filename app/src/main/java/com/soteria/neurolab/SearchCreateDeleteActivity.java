package com.soteria.neurolab;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.widget.TextView;

public class SearchCreateDeleteActivity extends AppCompatActivity {
    private TextView mTextMessage;

    final Fragment createPatientFragment = new CreatePatientFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = createPatientFragment;
    //Not implemented yet
    //final Fragment searchPatientFragment = new SearchPatientFragment();
    //final Fragment deletePatientFragment = new DeletePatientFragment();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search_patient:
                    mTextMessage.setText(getTitle());
                    //Search Patient not implemented yet
                    //fragmentManager.beginTransaction().hide(active).show(searchPatientFragment).commit();
                    //active = searchPatientFragment;
                    return true;
                case R.id.navigation_create_patient:
                    mTextMessage.setText(getTitle());
                    fragmentManager.beginTransaction().hide(active).show(createPatientFragment).commit();
                    active = createPatientFragment;
                    return true;
                case R.id.navigation_delete_patient:
                    mTextMessage.setText(getTitle());
                    //Delete Patient not implemented yet
                    //fragmentManager.beginTransaction().hide(active).show(deletePatientFragment).commit();
                    //active = deletePatientFragment;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_create_delete_page);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //This is part of the technique to hide a fragment without resetting it so that when you go back to it, it is still the same.
        // Once all are implemented, hide the createPatientFragment and unhide the searchPatientFragment as it is the home screen.
        //fragmentManager.beginTransaction().add(R.id.fragment_layout, searchPatientFragment, "1").hide(searchPatientFragment).commit();
        //fragmentManager.beginTransaction().add(R.id.fragment_layout, deletePatientFragment, "3").hide(deletePatientFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_layout, createPatientFragment, "2").commit();
    }

}
