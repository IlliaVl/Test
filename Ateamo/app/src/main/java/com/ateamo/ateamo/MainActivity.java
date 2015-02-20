package com.ateamo.ateamo;

import android.app.Activity;
import android.os.Bundle;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT_RIGHT);
        menu.setMenu(R.layout.menu_left);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow_left);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.setSecondaryMenu(R.layout.menu_right);
        menu.setSecondaryShadowDrawable(R.drawable.shadow_right);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
    }


}
