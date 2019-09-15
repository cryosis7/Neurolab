package com.soteria.neurolab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.soteria.neurolab.utilities.PasswordAuthentication;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int PASSWORD_LENGTH_MINIMUM = 6; // TODO: discuss length and move to better location.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Will enable / disable the sign in button when enough characters are entered.
        ((EditText) findViewById(R.id.login_password_edittext)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                findViewById(R.id.login_sign_in_button).setEnabled(charSequence.length() >= PASSWORD_LENGTH_MINIMUM);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void signInButtonHandler(View view) {
        char[] password = ((EditText) findViewById(R.id.login_password_edittext)).getText().toString().toCharArray();
        PasswordAuthentication authenticator = new PasswordAuthentication();

//        String encryptedPassword = hashPassword(password);
        Log.i(TAG, authenticator.hash(password));
        Log.i(TAG, "" + authenticator.authenticate(password, "$31$16$VGVtcFNhbHRUZW1wU2FsdGo2+yQCcD7wQwtZpgnKsNY"));
    }

    private String hashPassword(String password) {
        final int iterations = 65536;
        final int keyLength = 128;
        final String algorithm = "PBKDF2WithHmacSHA1";
        final byte[] temporarySalt = "TempSalt".getBytes(); // generateSalt(); //TODO: Salt needs to be stored somewhere - apparently can be public

        KeySpec keySpecs = new PBEKeySpec(password.toCharArray(), temporarySalt, iterations, keyLength);
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);
            byte[] hash = secretKeyFactory.generateSecret(keySpecs).getEncoded();
            SecretKey secretKey = new SecretKeySpec(hash, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            //TODO: store Cipher as field/static variable for re-use
            return Base64.encodeToString(cipher.doFinal(password.getBytes()), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Error while encrypting password: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generates a random salt
     * @return The salt that is used to randomise the encryption.
     */
    public static byte[] generateSalt () {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.encode(salt, Base64.DEFAULT);
    }
}