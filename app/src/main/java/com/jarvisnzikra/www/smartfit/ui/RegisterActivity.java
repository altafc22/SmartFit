package com.jarvisnzikra.www.smartfit.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jarvisnzikra.www.smartfit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    Button showPass;

    RadioGroup radioGroupGender;
    TextView tv_dob;
    EditText etUsername,etPassword,etName,etWeight,etHeight_ft,etHeight_in,etEmail,etMobile;

    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myCalendar2 = Calendar.getInstance();
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etName =findViewById(R.id.et_name);
        etHeight_ft =findViewById(R.id.et_height_ft);
        etHeight_in =findViewById(R.id.et_height_in);
        etWeight =findViewById(R.id.et_weight);
        radioGroupGender =findViewById(R.id.radioGroupGender);
        radioGroupGender =findViewById(R.id.radioGroupGender);
        tv_dob = findViewById(R.id.et_dob);
        etEmail =findViewById(R.id.et_email);
        etMobile =findViewById(R.id.et_mobile);
        etUsername =findViewById(R.id.et_username);
        etPassword =findViewById(R.id.et_password);
        etWeight =findViewById(R.id.et_weight);

        showPass = findViewById(R.id.showPassword);
        showPass.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(etPassword.length()>0){
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
            //    else
           //             showPass.setError("Please enter password");
                return true;
            }
        });


        final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setFromDate();
            }

        };

        tv_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,date1,myCalendar
                        .get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(myCalendar2.getTimeInMillis());
                datePickerDialog.show();
            }
        });
    }
    private void setFromDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tv_dob.setText(sdf.format(myCalendar.getTime()));
    }

    public void saveData(View v) {

        final String name = etName.getText().toString().trim();
        final String height_ft = etHeight_ft.getText().toString().trim();
        final String height_in = etHeight_in.getText().toString().trim();
        final String weight = etWeight.getText().toString().trim();
        final String gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
        final String dob = tv_dob.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String mobile = etMobile.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        //first we will do the validations

        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter name");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(height_ft)) {
            etHeight_ft.setError("Please enter height in foot");
            etHeight_ft.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(height_in)) {
            etHeight_in.setError("Please enter height in cm");
            etHeight_in.setText("0");
            etHeight_in.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(weight)) {
            etWeight.setError("Please enter height in cm");
            etWeight.requestFocus();
            return;
        }

        RadioButton rd = findViewById(R.id.rd_male);
        if (radioGroupGender.getCheckedRadioButtonId() == -1) {

            rd.setError("please select gender");
        } else {
            rd.setError(null);
        }

        if (TextUtils.isEmpty(dob)) {
            tv_dob.setError("Please enter date of birth");
            tv_dob.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter your email");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Please enter date of birth");
            etMobile.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Please enter username");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter a password");
            etPassword.requestFocus();
            return;
        }

        //if it passes all the validations
        /*
        class RegisterUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("email", email);
                params.put("name", name);
                params.put("height_ft",height_ft);
                params.put("height_in",height_in);
                params.put("weight",weight);
                params.put("gender", gender);
                params.put("dob",dob);
                params.put("mobile_no",mobile);
                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //hiding the progressbar after completion
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
                                userJson.getString("height_ft"),
                                userJson.getString("height_in"),
                                userJson.getString("weight"),
                                userJson.getString("gender"),
                                userJson.getString("dob"),
                                userJson.getString("mobile_no")
                        );

                        //storing the user in shared preferences
                        UserSharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }
    */
    }
}
