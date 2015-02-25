package com.ateamo.ateamo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_choose_login);
        Intent intent;
//        if (Member.getCurrent() == null) {
//            intent = new Intent(this, LoginActivity.class);
//        } else {
//            intent = new Intent(this, MainActivity.class);
//        }


        intent = new Intent(this, ChooseLoginActivity.class);


        startActivity(intent);
        finish();
    }
}
