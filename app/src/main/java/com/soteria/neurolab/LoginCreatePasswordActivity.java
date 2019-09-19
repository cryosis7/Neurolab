package com.soteria.neurolab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.soteria.neurolab.utilities.DisclaimerAlertDialog;

public class LoginCreatePasswordActivity extends AppCompatActivity {
    private static final String TAG = "LoginCreatePasswordActivity";

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private final LoginFragment loginFragment = new LoginFragment();
    private final CreatePasswordFragment createPasswordFragment = new CreatePasswordFragment();
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_create_password);
        setFragment(); // TODO: Could this be inline instead of function call or is it reused
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

    // TODO: Remove unneeded settings.
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

    // TODO: Handle back button pressed.

    private void setFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (activeFragment == null) {
            transaction.add(R.id.activity_login_create_password, loginFragment);
            transaction.add(R.id.activity_login_create_password, createPasswordFragment);
            activeFragment = loginFragment;
        }

        transaction.hide(activeFragment);
        activeFragment = (activeFragment instanceof LoginFragment) ? createPasswordFragment : loginFragment;

        transaction.show(activeFragment);
        getSupportActionBar().setTitle((activeFragment instanceof LoginFragment) ? "Login" : "Create Password");

        transaction.commit();
    }
}
