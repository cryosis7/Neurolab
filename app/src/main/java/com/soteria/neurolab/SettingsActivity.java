package com.soteria.neurolab;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity implements SettingsResetPasswordFragment.OnFragmentInteractionListener,
                                                                   SettingsSecurityQuestionsFragment.OnFragmentInteractionListener{

    final Fragment passwordFragment = new SettingsResetPasswordFragment();
    final Fragment questionsFragment = new SettingsSecurityQuestionsFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment active = passwordFragment;
    private ActionBar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        BottomNavigationView navView = findViewById(R.id.settings_nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(getSupportActionBar() != null ) {
            toolbar = getSupportActionBar();
            toolbar.setTitle("Reset Password");
            toolbar.setHomeButtonEnabled(true);
            toolbar.setDisplayHomeAsUpEnabled(true);

            fragmentManager.beginTransaction().add(R.id.fragment_layout_settings, passwordFragment, "2").hide(questionsFragment).commit();
            fragmentManager.beginTransaction().add(R.id.fragment_layout_settings, questionsFragment, "1").commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.settings_reset_option:
                    fragmentManager.beginTransaction().hide(active).attach(passwordFragment).show(passwordFragment).commit();
                    active = passwordFragment;
                    toolbar.setTitle(getString(R.string.settings_password_reset_title));
                    return true;
                case R.id.settings_questions_option:
                    fragmentManager.beginTransaction().hide(active).detach(passwordFragment).attach(questionsFragment).show(questionsFragment).commit();
                    active = questionsFragment;
                    toolbar.setTitle(getString(R.string.settings_textview_security_title));
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
