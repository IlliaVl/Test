package com.ateamo.ateamo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ateamo.core.Member;


public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (Member.getCurrent().getHash() == null) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
