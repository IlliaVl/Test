package com.ateamo.ateamo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Random;


/**
 * A login screen that offers login via email/password.
 */
public class ChooseLoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login);
    }



    public void emailLoginButtonClicked(View view) {
        Intent intent = new Intent(this, EmailLoginActivity.class);
        startActivity(intent);
    }



    public void signupButtonClicked(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }



    public void loginFacebookButtonClicked(View view) {
        Random rand = new Random();
        int randomNum = rand.nextInt(6);
        if (randomNum < 3) {
            Intent intent = new Intent(this, ConfirmAccountActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, CompleteAccountActivity.class);
            startActivity(intent);
        }
    }
}



