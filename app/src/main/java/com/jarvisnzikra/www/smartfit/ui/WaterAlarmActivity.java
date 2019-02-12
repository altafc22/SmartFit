package com.jarvisnzikra.www.smartfit.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jarvisnzikra.www.smartfit.R;

import java.text.DecimalFormat;

public class WaterAlarmActivity extends AppCompatActivity {

    EditText etWeight,etAge,etHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_alarm);
        etWeight = findViewById(R.id.et_weight);
        //etAge = findViewById(R.id.et_age);
        //etWeight = findViewById(R.id.et_height);
    }

    public void checkWater(View v)
    {
        double weight_in_kg,weight_in_lbs,water_in_ounces,extra_water,water_in_liter;
        int daily_workout_minute=0;

        if(etWeight.length()<1)
            etWeight.setError("Enter Weight in Kg.");

        weight_in_kg = Double.parseDouble(etWeight.getText().toString());

        weight_in_lbs = weight_in_kg*2.205; //weight in pounds
        water_in_ounces = weight_in_lbs*(0.66); // ounces of only per day water without exersice.
        extra_water = daily_workout_minute/(30*12); // ounces of additional water per day if you excersice every day(30 days).
        water_in_liter = (water_in_ounces+extra_water)/33.814;

        System.out.println("weight kg:"+weight_in_kg+
                            "\nwegght lbs: "+weight_in_lbs+
                            "\nounces: "+water_in_ounces+
                            "\nliter: "+water_in_liter);
        if(weight_in_kg>=1)
        {
            String result = new DecimalFormat("##.##").format(water_in_liter);
            Toast.makeText(WaterAlarmActivity.this,"you should drink "+result+" liter water each day.",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(WaterAlarmActivity.this,"Invalid Data",Toast.LENGTH_LONG).show();
        }

    }
}
