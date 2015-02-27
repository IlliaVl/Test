package com.ateamo.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ateamo.UI.login.ChooseLoginActivity;


public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
//        if (Member.getCurrent() == null) {
//            intent = new Intent(this, ChooseLoginActivity.class);
//        } else {
//            intent = new Intent(this, MainActivity.class);
//        }
        intent = new Intent(this, ChooseLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
