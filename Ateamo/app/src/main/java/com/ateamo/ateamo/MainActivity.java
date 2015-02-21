package com.ateamo.ateamo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;



public class MainActivity extends FragmentActivity {
    private final String SCHEDULE_TAB_ID = "schedule";
    private final String SCHEDULE_TAB_NAME_ID = "Schedule";
    private final String CHAT_TAB_ID = "chat";
    private final String CHAT_TAB_NAME_ID = "Chat";

    private SlidingMenu slidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabs();
        initImageLoader();
        initMenus();
    }



    private void initTabs() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                ScheduleFragment scheduleFragment = (ScheduleFragment) fragmentManager.findFragmentByTag(SCHEDULE_TAB_ID);
                ChatFragment chatFragment = (ChatFragment) fragmentManager.findFragmentByTag(CHAT_TAB_ID);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if(scheduleFragment!=null) {
                    fragmentTransaction.detach(scheduleFragment);
                }

                if(chatFragment!=null) {
                    fragmentTransaction.detach(chatFragment);
                }
                if(tabId.equalsIgnoreCase(SCHEDULE_TAB_ID)){

                    if(scheduleFragment == null){
                        fragmentTransaction.add(R.id.realtabcontent, new ScheduleFragment(), SCHEDULE_TAB_ID);
                    } else {
                        fragmentTransaction.attach(scheduleFragment);
                    }

                } else {
                    if(chatFragment==null){
                        fragmentTransaction.add(R.id.realtabcontent, new ChatFragment(), CHAT_TAB_ID);
                    }else{
                        fragmentTransaction.attach(chatFragment);
                    }
                }
                fragmentTransaction.commit();
            }
        };
        tabHost.setOnTabChangedListener(tabChangeListener);

        TabHost.TabSpec tSpecAndroid = tabHost.newTabSpec(SCHEDULE_TAB_ID);
        tSpecAndroid.setIndicator(SCHEDULE_TAB_NAME_ID,getResources().getDrawable(R.drawable.schedule));
        tSpecAndroid.setContent(new TabContent(getBaseContext()));
        tabHost.addTab(tSpecAndroid);

        TabHost.TabSpec tSpecApple = tabHost.newTabSpec(CHAT_TAB_ID);
        tSpecApple.setIndicator(CHAT_TAB_NAME_ID,getResources().getDrawable(R.drawable.chat));
        tSpecApple.setContent(new TabContent(getBaseContext()));
        tabHost.addTab(tSpecApple);

    }



    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }



    private void initMenus() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidingMenu.setMenu(R.layout.menu_left);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow_left);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setSecondaryMenu(R.layout.menu_right);
        slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow_right);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        fillMenus();
    }



    private void fillMenus() {
        final ListView teamsListview = (ListView) findViewById(R.id.teamsListView);
        final ArrayList<Team> list = Team.teams;
        final TeamArrayAdapter adapter = new TeamArrayAdapter(this, R.layout.list_item_team, Team.teams);
        teamsListview.setAdapter(adapter);
    }



    private class TeamArrayAdapter extends ArrayAdapter<Team> {

        public TeamArrayAdapter(Context context, int resource, ArrayList<Team> objects) {
            super(context, resource, objects);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_team, parent, false);
            }
            Team team = getItem(position);

            TextView teamNameTextView = (TextView)convertView.findViewById(R.id.teamNameTextView);
            teamNameTextView.setText(team.getName());

            ImageView teamBadgeImageView = (ImageView)convertView.findViewById(R.id.teamBadgeImageView);
            ImageLoader.getInstance().displayImage(team.getBadgeURL(), teamBadgeImageView);

            return convertView;
        }

    }



    public void openLeftMenu(View view) {
        slidingMenu.showMenu();
    }



    public void openRightMenu(View view) {
        slidingMenu.showSecondaryMenu();
    }



}
