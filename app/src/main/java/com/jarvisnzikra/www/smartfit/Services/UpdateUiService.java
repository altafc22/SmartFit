package com.jarvisnzikra.www.smartfit.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jarvisnzikra.www.smartfit.Database;
import com.jarvisnzikra.www.smartfit.util.Util;

import java.util.Date;

public class UpdateUiService extends Service {


    private static final String TAG = "UpdateUiService";
    public static final String BROADCAST_ACTION = "com.jarvisnzikra.www.smartfit.displayevent";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;
    Database db;

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        db = Database.getInstance(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
    }
    private void FetchFootsteps() {
        Log.d(TAG, "entered FetchFootsteps");
        //intent.putExtra("time", new Date().toLocaleString());
        //intent.putExtra("counter", String.valueOf(++counter));
        Database db = Database.getInstance(this);
        int steps = Math.max(db.getCurrentSteps() + db.getSteps(Util.getToday()), 0);
        intent.putExtra("counter", String.valueOf(steps));
        db.close();
        sendBroadcast(intent);
    }
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            FetchFootsteps();
            handler.postDelayed(this, 1000); // 1 seconds
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
    }
}
