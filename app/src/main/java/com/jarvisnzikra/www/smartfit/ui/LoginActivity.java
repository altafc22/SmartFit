package com.jarvisnzikra.www.smartfit.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.jarvisnzikra.www.smartfit.RequestHandler;
import com.jarvisnzikra.www.smartfit.URLs;
import com.jarvisnzikra.www.smartfit.User;
import com.jarvisnzikra.www.smartfit.UserSharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    Button showPass;
    EditText etPassword,etUsername;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //if the user is already logged in we will directly start the profile activity
        if (UserSharedPrefManager.getInstance(this).isLoggedIn()) {
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

        //if everything is fine

        class UserLogin extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("password"),
                                userJson.getString("email"),
                                userJson.getString("name"),
                                userJson.getString("weight"),
                                userJson.getString("height"),
                                userJson.getString("gender"),
                                userJson.getString("dob"),
                                userJson.getString("mobile_no")
                        );

                        //storing the user in shared preferences
                        UserSharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }
        UserLogin ul = new UserLogin();
        ul.execute();
    }

    public void gotoRegister(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }
}
