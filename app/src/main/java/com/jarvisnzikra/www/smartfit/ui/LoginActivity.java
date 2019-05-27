package com.jarvisnzikra.www.smartfit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jarvisnzikra.www.smartfit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_PASSWORD = "keyuserpassword";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_NAME = "keyname";
    private static final String KEY_WEIGHT = "keyweight";
    private static final String KEY_HEIGHT_FT = "keyheight_ft";
    private static final String KEY_HEIGHT_IN = "keyheight_in";
    private static final String KEY_GENDER = "keygender";
    private static final String KEY_DOB = "keydob";
    private static final String KEY_MOBILE = "keymobile";

    Button showPass;
    EditText etPassword,etUsername;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




        //if the user is already logged in we will directly start the profile activity
        if (isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
            return;
        }

        etPassword =findViewById(R.id.et_password);
        etUsername =findViewById(R.id.et_username);
        showPass = findViewById(R.id.showPassword);


        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(etPassword.length()>0)
                {
                    switch ( event.getAction() ) {
                        case MotionEvent.ACTION_DOWN:
                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                            etPassword.setTypeface(Typeface.SANS_SERIF);
                            etPassword.setTextSize(17);
                            break;
                        case MotionEvent.ACTION_UP:
                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            etPassword.setTypeface(Typeface.SANS_SERIF);
                            etPassword.setTextSize(17);
                            break;
                    }
                }
               // else
               //     showPass.setError("Please enter password");
                return true;
            }
        });
    }

    public void checkLogin(View v){
        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
        //finish();

        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        //validating inputs
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Please enter your username");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Please enter your password");
            etPassword.requestFocus();
            return;
        }


       /* SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_HEIGHT_FT, user.getHeight_ft());
        editor.putString(KEY_HEIGHT_IN, user.getHeight_in());
        editor.putString(KEY_WEIGHT, user.getWeight());
        editor.putString(KEY_GENDER, user.getGender());
        editor.putString(KEY_DOB, user.getDob());
        editor.putString(KEY_MOBILE, user.getMobile_no());
        editor.apply();
        */

                /*
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
                sharedPreferences.getInt(KEY_ID, -1);
                sharedPreferences.getString(KEY_USERNAME, null);
                sharedPreferences.getString(KEY_PASSWORD, null);
                sharedPreferences.getString(KEY_EMAIL, null);
                sharedPreferences.getString(KEY_NAME, null);
                sharedPreferences.getString(KEY_WEIGHT, null);
                sharedPreferences.getString(KEY_HEIGHT_FT, null);
                sharedPreferences.getString(KEY_HEIGHT_IN, null);
                sharedPreferences.getString(KEY_GENDER, null);
                sharedPreferences.getString(KEY_DOB, null);
                sharedPreferences.getString(KEY_MOBILE, null);
                */
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    public void gotoRegister(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }
}
