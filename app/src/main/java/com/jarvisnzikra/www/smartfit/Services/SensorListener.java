package com.jarvisnzikra.www.smartfit.Services;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.jarvisnzikra.www.smartfit.BuildConfig;
import com.jarvisnzikra.www.smartfit.Database;
import com.jarvisnzikra.www.smartfit.ShutdownReceiver;
import com.jarvisnzikra.www.smartfit.util.Logger;
import com.jarvisnzikra.www.smartfit.util.Util;

import java.util.Date;

/**
 * Background service which keeps the step-sensor listener alive to always get
 * the number of steps since boot.
 * <p/>
 * This service won't be needed any more if there is a way to read the
 * step-value without waiting for a sensor event
 */
public class SensorListener extends Service implements SensorEventListener {

    private final static long MICROSECONDS_IN_ONE_MINUTE = 60000000;
    private final static long SAVE_OFFSET_TIME = AlarmManager.INTERVAL_HOUR;
    private final static int SAVE_OFFSET_STEPS = 500;

    private static int steps;
    private static int lastSaveSteps;
    private static long lastSaveTime;

    private final BroadcastReceiver shutdownReceiver = new ShutdownReceiver();

    @Override
    public void onAccuracyChanged(final Sensor sensor, int accuracy) {
        // nobody knows what happens here: step value might magically decrease
        // when this method is called...
        if (BuildConfig.DEBUG) Logger.log(sensor.getName() + " accuracy changed: " + accuracy);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (event.values[0] > Integer.MAX_VALUE) {
            if (BuildConfig.DEBUG) Logger.log("probably not a real value: " + event.values[0]);
            return;
        } else {
            steps = (int) event.values[0];
            System.out.println("Steeeeps:"+steps);
            updateIfNecessary();
            Intent i = new Intent("android.intent.action.MAIN").putExtra("footstep",  steps);
            this.sendBroadcast(i);
        }
    }

    /**
     * @return true, if notification was updated
     */
    private boolean updateIfNecessary() {

        System.out.println("Steps in Update :"+steps);
        System.out.println("lastSaveSteps in Update :"+lastSaveSteps);
        System.out.println("SAVE_OFFSET_STEPS in Update :"+SAVE_OFFSET_STEPS);
        System.out.println("lastSaveSteps+SAVE_OFFSET_STEPS :"+(lastSaveSteps+SAVE_OFFSET_STEPS));
        System.out.println("lastSaveTime+SAVE_OFFSET_TIME :"+(lastSaveTime+SAVE_OFFSET_TIME));
        System.out.println("Current_TIME :"+System.currentTimeMillis());

       if (steps > lastSaveSteps + SAVE_OFFSET_STEPS ||
                (steps > 0 && System.currentTimeMillis() > lastSaveTime + SAVE_OFFSET_TIME)) {
            if (BuildConfig.DEBUG) Logger.log(
                    "saving steps: steps=" + steps + " lastSave=" + lastSaveSteps +
                            " lastSaveTime=" + new Date(lastSaveTime));
            Database db = Database.getInstance(this);
            if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
                int pauseDifference = steps -
                        getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                                .getInt("pauseCount", steps);
                db.insertNewDay(Util.getToday(), steps - pauseDifference);
                if (pauseDifference > 0) {
                    // update pauseCount for the new day
                    getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit()
                            .putInt("pauseCount", steps).commit();
                }
            }
            db.saveCurrentSteps(steps);
            db.close();
            lastSaveSteps = steps;
            lastSaveTime = System.currentTimeMillis();
            System.out.println("lastSaveSteps:"+lastSaveSteps);
            System.out.println("lastSaveTime:"+lastSaveTime);

            //showNotification(); // update notification
            //artService(new Intent(this, WidgetUpdateService.class));
            return true;
        } else {
            return false;
        }
    }


    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        reRegisterSensor();
        System.out.println("Service Started__________");
        registerBroadcastReceiver();
        if (!updateIfNecessary()) {
                System.out.println("Service Notification__________");
            //showNotification();
        }

        // restart service every hour to save the current step count
        long nextUpdate = Math.min(Util.getTomorrow(),
                System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR);
        System.out.println("next update: " + new Date(nextUpdate));
        //if (BuildConfig.DEBUG) Logger.log("next update: " + new Date(nextUpdate).toLocaleString());
        //AlarmManager am =
         //       (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
       // PendingIntent pi = PendingIntent
        //        .getService(getApplicationContext(), 2, new Intent(this, SensorListener.class),
        //                PendingIntent.FLAG_UPDATE_CURRENT);
        /*if (Build.VERSION.SDK_INT >= 23) {
            API23Wrapper.setAlarmWhileIdle(am, AlarmManager.RTC, nextUpdate, pi);
        } else {
            am.set(AlarmManager.RTC, nextUpdate, pi);
        }*/

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("SensorListener onCreate");
        if (BuildConfig.DEBUG) Logger.log("SensorListener onCreate");
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (BuildConfig.DEBUG) Logger.log("sensor service task removed");
        // Restart service in 500 ms
        ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                .set(AlarmManager.RTC, System.currentTimeMillis() + 500, PendingIntent
                        .getService(this, 3, new Intent(this, SensorListener.class), 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (BuildConfig.DEBUG) Logger.log("SensorListener onDestroy");
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
            e.printStackTrace();
        }
    }



    private void registerBroadcastReceiver() {
        if (BuildConfig.DEBUG) Logger.log("register broadcastreceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SHUTDOWN);
        registerReceiver(shutdownReceiver, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void reRegisterSensor() {
        if (BuildConfig.DEBUG) Logger.log("re-register sensor listener");
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        try {
            sm.unregisterListener(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Logger.log(e);
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            Logger.log("step sensors: " + sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size());
            if (sm.getSensorList(Sensor.TYPE_STEP_COUNTER).size() < 1) return; // emulator
            Logger.log("default: " + sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER).getName());
        }

        // enable batching with delay of max 5 min
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_NORMAL, (int) (5 * MICROSECONDS_IN_ONE_MINUTE));
    }
}
