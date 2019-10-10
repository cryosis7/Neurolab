package com.soteria.neurolab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * The root container class for the logon/create password screens.
 * Each of those two are loaded as fragments inside of this activity and they are toggled as
 * needed.
 *
 * @author Scott Curtis
 */
public class LoginCreatePasswordActivity extends AppCompatActivity {
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private final LoginFragment loginFragment = new LoginFragment();
    private final CreatePasswordFragment createPasswordFragment = new CreatePasswordFragment();
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_create_password);
        switchFragment();
    }

    /**
     * Overrides the default back button action to exit the application instead of returning
     * to the previous screen.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Will switch the currently displayed fragment.
     * When no password exists in the beginning, it will load the create password fragment.
     */
    void switchFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (activeFragment == null) {
            transaction.add(R.id.activity_login_create_password, loginFragment);
            transaction.add(R.id.activity_login_create_password, createPasswordFragment);

            SharedPreferences pref = this.getSharedPreferences(getString(R.string.shared_preferences_filename), MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.apply();

            // Initialising the active fragment to the opposite of what we want since they'll be toggled 3 lines later.
            activeFragment = pref.getString("passwordHash", null) == null ? loginFragment : createPasswordFragment;
        }

        transaction.hide(activeFragment);
        activeFragment = (activeFragment instanceof LoginFragment) ? createPasswordFragment : loginFragment;

        transaction.show(activeFragment);
        getSupportActionBar().setTitle((activeFragment instanceof LoginFragment) ? "Login" : "Create Password");

        transaction.commit();
    }
}
