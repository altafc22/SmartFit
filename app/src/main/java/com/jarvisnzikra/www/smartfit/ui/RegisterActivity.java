package com.jarvisnzikra.www.smartfit.ui;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.jarvisnzikra.www.smartfit.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    Button showPass;
    TextView tv_dob;
    EditText etPassword,etUsername,etName;
    final Calendar myCalendar = Calendar.getInstance();
    final Calendar myCalendar2 = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPassword =findViewById(R.id.et_password);
        etName =findViewById(R.id.et_name);
        etUsername =findViewById(R.id.et_username);
        showPass = findViewById(R.id.showPassword);
        tv_dob = findViewById(R.id.et_dob);

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

    public void saveData(View v)
    {

    }

}
