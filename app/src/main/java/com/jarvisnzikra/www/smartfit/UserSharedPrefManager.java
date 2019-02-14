package com.jarvisnzikra.www.smartfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jarvisnzikra.www.smartfit.ui.LoginActivity;

public class UserSharedPrefManager {

        //the constants
        private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
        private static final String KEY_ID = "keyid";
        private static final String KEY_USERNAME = "keyusername";
        private static final String KEY_PASSWORD = "keyuserpassword";
        private static final String KEY_EMAIL = "keyemail";
        private static final String KEY_NAME = "keyname";
        private static final String KEY_WEIGHT = "keyweight";
        private static final String KEY_HEIGHT = "keyheight";
        private static final String KEY_GENDER = "keygender";
        private static final String KEY_DOB = "keydob";
        private static final String KEY_MOBILE = "keymobile";

        private static UserSharedPrefManager mInstance;
        private static Context mCtx;

        private UserSharedPrefManager(Context context) {
            mCtx = context;
        }

        public static synchronized UserSharedPrefManager getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new UserSharedPrefManager(context);
            }
            return mInstance;
        }

        //method to let the user login
        //this method will store the user data in shared preferences
        public void userLogin(User user) {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_ID, user.getId());
            editor.putString(KEY_USERNAME, user.getUsername());
            editor.putString(KEY_PASSWORD, user.getPassword());
            editor.putString(KEY_EMAIL, user.getEmail());
            editor.putString(KEY_NAME, user.getName());
            editor.putString(KEY_HEIGHT, user.getHeight());
            editor.putString(KEY_WEIGHT, user.getWeight());
            editor.putString(KEY_GENDER, user.getGender());
            editor.putString(KEY_DOB, user.getDob());
            editor.putString(KEY_MOBILE, user.getMobile_no());
            editor.apply();
        }

        //this method will checker whether user is already logged in or not
        public boolean isLoggedIn() {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return sharedPreferences.getString(KEY_USERNAME, null) != null;
        }

        //this method will give the logged in user
        public User getUser() {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return new User(
                    sharedPreferences.getInt(KEY_ID, -1),
                    sharedPreferences.getString(KEY_USERNAME, null),
                    sharedPreferences.getString(KEY_PASSWORD, null),
                    sharedPreferences.getString(KEY_EMAIL, null),
                    sharedPreferences.getString(KEY_NAME, null),
                    sharedPreferences.getString(KEY_WEIGHT, null),
                    sharedPreferences.getString(KEY_HEIGHT, null),
                    sharedPreferences.getString(KEY_GENDER, null),
                    sharedPreferences.getString(KEY_DOB, null),
                    sharedPreferences.getString(KEY_MOBILE, null)
            );
        }
        //this method will logout the user
        public void logout() {
            SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
        }

}
