package com.jarvisnzikra.www.smartfit.ui;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jarvisnzikra.www.smartfit.BuildConfig;
import com.jarvisnzikra.www.smartfit.Database;
import com.jarvisnzikra.www.smartfit.R;
import com.jarvisnzikra.www.smartfit.Services.SensorListener;
import com.jarvisnzikra.www.smartfit.Services.UpdateUiService;
import com.jarvisnzikra.www.smartfit.ui.Adapters.SectionsPageAdapter;
import com.jarvisnzikra.www.smartfit.util.Logger;
import com.jarvisnzikra.www.smartfit.util.Util;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.jarvisnzikra.www.smartfit.ui.PedometerActivity.formatter;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    int i=0;
    TextView tv_footstepCount;

    private static final String TAG = "MainActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    Database db;


    final static int DEFAULT_GOAL = 10000;
    final static float DEFAULT_STEP_SIZE = Locale.getDefault() == Locale.US ? 2.5f : 75f;
    final static String DEFAULT_STEP_UNIT = Locale.getDefault() == Locale.US ? "ft" : "cm";
    private int todayOffset, total_start, goal, since_boot, total_days;

    TextView tv_distance,tv_time;
    boolean running = false;
    double random_footstep;
    private int milestoneStep;
    private BroadcastReceiver mReceiver;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_footstepCount = findViewById(R.id.tv_footstepCount);
        tv_distance = findViewById(R.id.tv_distance);
        tv_time = findViewById(R.id.tv_time);
        db = Database.getInstance(this);

        intent = new Intent(this, UpdateUiService.class);

        if(!isMyServiceRunning(SensorListener.class)) {
            startService(new Intent(getBaseContext(), SensorListener.class));
        }

      //Fragment Code
        /*
        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.apple);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_jogging);
        tabLayout.getTabAt(2).setIcon(R.drawable.fire);
        tabLayout.getTabAt(3).setIcon(R.drawable.footstep);
        */

        db = Database.getInstance(this);

        TextView tv_distance = (TextView) findViewById(R.id.tv_distance);
        stepsDistanceChanged();
        this.startService(new Intent(this, SensorListener.class));

    }

    private void stepsDistanceChanged() {

            String unit = getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                    .getString("stepsize_unit", DEFAULT_STEP_UNIT);
            if (unit.equals("cm")) {
                unit = "km";
            } else {
                unit = "mi";
            }
            ((TextView) findViewById(R.id.unit)).setText(unit);
        updateRunningDistance();
    }


    private void updateRunningDistance() {
        Date date_time = Calendar.getInstance().getTime();
        String time = new SimpleDateFormat("hh : mm a", Locale.getDefault()).format(date_time);
        String date = new SimpleDateFormat("EEEE dd-MM-yyyy", Locale.getDefault()).format(date_time);
        tv_time.setText(time+"\n"+date);
        if (BuildConfig.DEBUG) Logger.log("UI - update steps: " + since_boot);
        // todayOffset might still be Integer.MIN_VALUE on first start
        int steps_today = Math.max(todayOffset + since_boot, 0);
            // update only every 10 steps when displaying distance
            SharedPreferences prefs = getSharedPreferences("pedometer", Context.MODE_PRIVATE);
            float stepsize = prefs.getFloat("stepsize_value", DEFAULT_STEP_SIZE);
            float distance_today = steps_today * stepsize;
            if (prefs.getString("stepsize_unit", DEFAULT_STEP_UNIT)
                    .equals("cm")) {
                distance_today /= 100000;
            } else {
                distance_today /= 5280;
            }
            tv_distance.setText(formatter.format(distance_today));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isMyServiceRunning(SensorListener.class)) {
            startService(new Intent(getBaseContext(), SensorListener.class));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();

        if(!isMyServiceRunning(SensorListener.class)) {
            startService(new Intent(getBaseContext(), SensorListener.class));
        }

        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(UpdateUiService.BROADCAST_ACTION));


        Database db = Database.getInstance(this);

        if (BuildConfig.DEBUG) db.logState();
        // read todays offset
        todayOffset = db.getSteps(Util.getToday());

        SharedPreferences prefs =
                this.getSharedPreferences("pedometer", Context.MODE_PRIVATE);

        //goal = prefs.getInt("goal", Fragment_Settings.DEFAULT_GOAL);
        since_boot = db.getCurrentSteps();
        int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

        // register a sensorlistener to live update the UI if a step is taken
        SensorManager sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (sensor == null) {
            new AlertDialog.Builder(this).setTitle("no sensor")
                    .setMessage("no sensor explain")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(final DialogInterface dialogInterface) {
                            finish();
                        }
                    }).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
        }

        since_boot -= pauseDifference;

        total_start = db.getTotalWithoutToday();
        total_days = db.getDays();

        db.close();

        stepsDistanceChanged();

    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);

        try {
            SensorManager sm =
                    (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
        }
        Database db = Database.getInstance(this);
        db.saveCurrentSteps(since_boot);
        db.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };


    private void updateUI(Intent intent) {
        System.out.println("counter: "+intent.getStringExtra("counter"));
        tv_footstepCount.setText(intent.getStringExtra("counter"));
        //String footstep = intent.getStringExtra("counter");
        //Log.d(TAG, footstep);
        //tv_footstepCount.setText(footstep);
        //tv_footstepCount.setText(footstep);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void gotoWaterAlarmActivity(View v)
    {
        startActivity(new Intent(MainActivity.this, WaterAlarmActivity.class));
    }

    public void gotoFootstepActivity(View v)
    {
        startActivity(new Intent(MainActivity.this, PedometerActivity.class));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (BuildConfig.DEBUG) Logger.log(
                "UI - sensorChanged | todayOffset: " + todayOffset + " since boot: " +
                        event.values[0]);
        if (event.values[0] > Integer.MAX_VALUE || event.values[0] == 0) {
            return;
        }
        if (todayOffset == Integer.MIN_VALUE) {
            // no values for today
            // we dont know when the reboot was, so set todays steps to 0 by
            // initializing them with -STEPS_SINCE_BOOT
            todayOffset = -(int) event.values[0];
            Database db = Database.getInstance(this);
            db.insertNewDay(Util.getToday(), (int) event.values[0]);
            db.close();
        }
        since_boot = (int) event.values[0];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    //fragment Code
    /*
    private void setupViewPager(ViewPager mViewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabOneFragment());
        adapter.addFragment(new TabTwoFragment());
        adapter.addFragment(new TabThreeFragment());
        adapter.addFragment(new TabFourFragment());
        mViewPager.setAdapter(adapter);
    }*/
}
