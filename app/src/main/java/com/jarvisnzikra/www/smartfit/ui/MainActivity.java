package com.jarvisnzikra.www.smartfit.ui;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jarvisnzikra.www.smartfit.Database;
import com.jarvisnzikra.www.smartfit.R;
import com.jarvisnzikra.www.smartfit.Services.SensorListener;
import com.jarvisnzikra.www.smartfit.Services.UpdateUiService;
import com.jarvisnzikra.www.smartfit.ui.Adapters.SectionsPageAdapter;


public class MainActivity extends AppCompatActivity {
    int i=0;
    TextView tv_footstepCount,tv_waterCount,tv_heartRateCount,tv_sleepCount;

    private static final String TAG = "MainActivity";
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    Database db;
    SensorManager sensorManager;

    TextView footstep_number;
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isMyServiceRunning(SensorListener.class)) {
            startService(new Intent(getBaseContext(), SensorListener.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isMyServiceRunning(SensorListener.class)) {
            startService(new Intent(getBaseContext(), SensorListener.class));
        }

        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(UpdateUiService.BROADCAST_ACTION));
        //toggleLoop();
        //registering your receiver
       /* running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Sensor not found.", Toast.LENGTH_LONG).show();
        }*/
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(intent);
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
