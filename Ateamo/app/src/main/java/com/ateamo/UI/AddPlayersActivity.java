package com.ateamo.UI;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.ateamo.ateamo.R;

public class AddPlayersActivity extends FragmentActivity {
    private final String EMAIL_TAB_ID = "email";
    private final String EMAIL_TAB_NAME_ID = "Email";
    private final String NUMBER_TAB_ID = "number";
    private final String NUMBER_TAB_NAME_ID = "Number";

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_players);
        initTabs();
    }



    private void initTabs() {
        tabHost = (TabHost) findViewById(R.id.addPlayersTabHost);
        tabHost.setup();
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                ContactsFragment emailFragment = (ContactsFragment) fragmentManager.findFragmentByTag(EMAIL_TAB_ID);
                ContactsFragment numberFragment = (ContactsFragment) fragmentManager.findFragmentByTag(NUMBER_TAB_ID);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if(emailFragment != null) {
                    fragmentTransaction.detach(emailFragment);
                }

                if(numberFragment != null) {
                    fragmentTransaction.detach(numberFragment);
                }

                if(tabId.equalsIgnoreCase(EMAIL_TAB_ID)) {
                    if(emailFragment == null){
                        fragmentTransaction.add(R.id.realtabcontent, new ContactsFragment(), EMAIL_TAB_ID);
                    } else {
                        fragmentTransaction.attach(emailFragment);
                    }

                } else {
                    if(numberFragment == null) {
                        fragmentTransaction.add(R.id.realtabcontent, new ContactsFragment(), NUMBER_TAB_ID);
                    } else {
                        fragmentTransaction.attach(numberFragment);
                    }
                }
                fragmentTransaction.commit();
            }
        };
        tabHost.setOnTabChangedListener(tabChangeListener);
        tabHost.addTab(getTabSpec(tabHost.newTabSpec(EMAIL_TAB_ID), R.drawable.tab_indicator, EMAIL_TAB_NAME_ID, R.drawable.schedule_icon));
        tabHost.addTab(getTabSpec(tabHost.newTabSpec(NUMBER_TAB_ID), R.drawable.tab_indicator, NUMBER_TAB_NAME_ID, R.drawable.chat_icon));
    }



    private TabHost.TabSpec getTabSpec(TabHost.TabSpec spec, int resourceId, String tabName, int tabIcon) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        view.setBackgroundResource(resourceId);
        ((TextView)view.findViewById(R.id.tabTextView)).setText(tabName);
        ((ImageView)view.findViewById(R.id.tabImageView)).setBackgroundResource(tabIcon);
        spec.setContent(new TabContent(getBaseContext()));
        spec.setIndicator(view);
        return spec;
    }
}
