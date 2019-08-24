package com.soteria.neurolab;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SearchCreateDeleteActivity extends AppCompatActivity implements  CreatePatientFragment.OnFragmentInteractionListener,
        DeletePatientFragment.OnFragmentInteractionListener,
        SearchPatientFragment.OnFragmentInteractionListener {

    final Fragment createPatientFragment = new CreatePatientFragment();
    final Fragment searchPatientFragment = new SearchPatientFragment();
    final Fragment deletePatientFragment = new DeletePatientFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = createPatientFragment;

    private ActionBar toolbar;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search_patient:
                    fragmentManager.beginTransaction().hide(active).show(searchPatientFragment).commit();
                    active = searchPatientFragment;
                    toolbar.setTitle("Search Patient");
                    return true;
                case R.id.navigation_create_patient:
                    fragmentManager.beginTransaction().hide(active).show(createPatientFragment).commit();
                    active = createPatientFragment;
                    toolbar.setTitle("Create Patient");
                    return true;
                case R.id.navigation_delete_patient:
                    fragmentManager.beginTransaction().hide(active).show(deletePatientFragment).commit();
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

        toolbar = getSupportActionBar();
        toolbar.setHomeButtonEnabled(true);
        toolbar.setDisplayHomeAsUpEnabled(true);

        fragmentManager.beginTransaction().add(R.id.fragment_layout, createPatientFragment, "2").hide(createPatientFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_layout, deletePatientFragment, "3").hide(deletePatientFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragment_layout, searchPatientFragment, "1").commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try {
            getMenuInflater().inflate(R.menu.overflow_menu, menu);
            return true;
        } catch ( Exception e ) {
            Toast.makeText(getApplicationContext(),"EXCEPTION " + e + " occurred!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean onOptionsItemSelected(MenuItem menu)
    {
        try {
            switch(menu.getItemId()) {
                case R.id.action_disclaimer:
                    final AlertDialog.Builder disclaimerBuilder = new AlertDialog.Builder(this );
                    disclaimerBuilder.setTitle("Disclaimer");
                    disclaimerBuilder.setMessage("Neurolab by Soteria is designed as an assistant tool only.\n" +
                            "It should not be relied upon for treatment, diagnosis or professional medical advice.\n" +
                            "If you have any queries about its use, please consult your local physician or health care provider.\n\n" +
                            "By accessing and using this app, you have confirmed that you have read, understood and accepted the above statement.");
                    disclaimerBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()  {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    final AlertDialog showDisclaimer = disclaimerBuilder.create();
                    showDisclaimer.setOnShowListener( new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface arg0 ) {
                            showDisclaimer.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    });
                    showDisclaimer.show();
                    return true;
                case R.id.action_logout:
                    //TODO link to logout, remove toast afterwards
                    Toast.makeText(getApplicationContext(), "LOGOUT PRESSED - Going to log in screen", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(this, //TODO add link to log in here));
                    return true;
                default:
                    return false;
            }
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(),"EXCEPTION " + e + " occurred!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
