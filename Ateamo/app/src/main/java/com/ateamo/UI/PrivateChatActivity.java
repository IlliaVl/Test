package com.ateamo.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ateamo.ateamo.R;
import com.ateamo.core.Member;
import com.ateamo.definitions.Consts;


public class PrivateChatActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Member member = (Member)intent.getSerializableExtra(Consts.OPPONENT_EXTRA_ID);
            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setOpponent(member);
            getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
        }
    }
}
