package com.jarvisnzikra.www.smartfit.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jarvisnzikra.www.smartfit.R;

public class LoginActivity extends AppCompatActivity {

    Button showPass;
    EditText etPassword,etUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void gotoRegister(View v){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }
}
