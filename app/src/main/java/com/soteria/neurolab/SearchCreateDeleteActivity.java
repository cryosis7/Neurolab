package com.soteria.neurolab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.soteria.neurolab.database.DatabaseAccess;
import com.soteria.neurolab.utilities.DisclaimerAlertDialog;

public class SearchCreateDeleteActivity extends AppCompatActivity implements  CreatePatientFragment.OnFragmentInteractionListener,
        DeletePatientFragment.OnFragmentInteractionListener,
        SearchPatientFragment.OnFragmentInteractionListener {

    final Fragment createPatientFragment = new CreatePatientFragment();
    final Fragment searchPatientFragment = new SearchPatientFragment();
    final Fragment deletePatientFragment = new DeletePatientFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = searchPatientFragment;

    public static DatabaseAccess db;

    private ActionBar toolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search_patient:
                    fragmentManager.beginTransaction().hide(active).detach(searchPatientFragment).attach(searchPatientFragment).show(searchPatientFragment).commit();
                    active = searchPatientFragment;
                    toolbar.setTitle("Search Patient");
                    return true;
                case R.id.navigation_create_patient:
                    fragmentManager.beginTransaction().hide(active).show(createPatientFragment).commit();
                    active = createPatientFragment;
                    toolbar.setTitle("Create Patient");
                    return true;
                case R.id.navigation_delete_patient:
                    fragmentManager.beginTransaction().hide(active).detach(deletePatientFragment).attach(deletePatientFragment).show(deletePatientFragment).commit();
                    active = deletePatientFragment;
                    toolbar.setTitle("Delete Patient");
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
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        db = DatabaseAccess.getInstance(getApplicationContext());

        toolbar = getSupportActionBar();
        toolbar.setTitle("Search Patient");

        fragmentManager.beginTransaction().add(R.id.fragment_layout, createPatientFragment, "2").hide(createPatientFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_layout, deletePatientFragment, "3").hide(deletePatientFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_layout, searchPatientFragment, "1").commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*Grabs the information from the overflow_menu resource in the menu folder and sets them
          to the action bar*/
        try {
            getMenuInflater().inflate(R.menu.overflow_menu, menu);
            return true;
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "EXCEPTION " + e + " occurred!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            case R.id.action_disclaimer:
                DisclaimerAlertDialog dad = new DisclaimerAlertDialog();
                dad.showDisclaimer(this, getResources());
                return true;
            //If the log out button is pressed, send the user to the log in screen
            case R.id.action_logout:
                 startActivity(new Intent(this, LoginCreatePasswordActivity.class));
                 finish();
                return true;
            //If an unknown option is selected, display an error to the user
            default:
                Toast.makeText(getApplicationContext(), "INVALID - Option not valid or completed", Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    /** Action that occurs when the back button on the device or app is pressed on search, create or
    delete fragments. Hardcoded to prevent erroneous data from being selected if going back to the
    view patients screen after a patient has been deleted.
    */
    @Override

    public void onBackPressed()
    {
        startActivity(new Intent(this, LoginCreatePasswordActivity.class));
        finish();
    }
}
