package com.ateamo.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.ateamo.adapters.MembersAdapter;
import com.ateamo.adapters.TeamsAdapter;
import com.ateamo.ateamo.R;
import com.ateamo.core.Member;
import com.ateamo.core.Team;
import com.ateamo.definitions.Consts;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;



public class MainActivity extends FragmentActivity {
    private static MainActivity instance = null;

    private final int SCHEDULE_TAB_NUMBER = 0;
    private final int CHAT_TAB_NUMBER = 1;

    private final String SCHEDULE_TAB_ID = "schedule";
    private final String SCHEDULE_TAB_NAME_ID = "Schedule";
    private final String CHAT_TAB_ID = "chat";
    private final String CHAT_TAB_NAME_ID = "Chat";

    private SlidingMenu slidingMenu;
    private ChatFragment chatFragment;
    private TabHost tabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        initTabs();
        initImageLoader();
        initMenus();
    }



    private void initTabs() {
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                ScheduleFragment scheduleFragment = (ScheduleFragment) fragmentManager.findFragmentByTag(SCHEDULE_TAB_ID);
                chatFragment = (ChatFragment) fragmentManager.findFragmentByTag(CHAT_TAB_ID);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if(scheduleFragment != null) {
                    fragmentTransaction.detach(scheduleFragment);
                }

                if(chatFragment != null) {
                    fragmentTransaction.detach(chatFragment);
                }
                if(tabId.equalsIgnoreCase(SCHEDULE_TAB_ID)) {
                    if(scheduleFragment == null){
                        fragmentTransaction.add(R.id.realtabcontent, new ScheduleFragment(), SCHEDULE_TAB_ID);
                    } else {
                        fragmentTransaction.attach(scheduleFragment);
                    }

                } else {
                    if(chatFragment == null) {
                        chatFragment = new ChatFragment();
                        fragmentTransaction.add(R.id.realtabcontent, chatFragment, CHAT_TAB_ID);
                    } else {
                        fragmentTransaction.attach(chatFragment);
                    }
                }
                fragmentTransaction.commit();
            }
        };
        tabHost.setOnTabChangedListener(tabChangeListener);
        tabHost.addTab(getTabSpec(tabHost.newTabSpec(SCHEDULE_TAB_ID), R.drawable.tab_indicator, SCHEDULE_TAB_NAME_ID, R.drawable.schedule_icon));
        tabHost.addTab(getTabSpec(tabHost.newTabSpec(CHAT_TAB_ID), R.drawable.tab_indicator, CHAT_TAB_NAME_ID, R.drawable.chat_icon));
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
        fillLeftMenu();
        fillRightMenu();
    }



    public void fillLeftMenu() {
        final ImageView currentMemberPictureImageView = (ImageView) findViewById(R.id.currentMemberPictureImageView);
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        ImageLoader.getInstance().displayImage(Member.getCurrent().getProfilePictureURL(), currentMemberPictureImageView, options);
        final TextView currentMemberNameTextView = (TextView) findViewById(R.id.currentMemberNameTextView);
        currentMemberNameTextView.setText(Member.getCurrent().getName());
        final ListView teamsListView = (ListView) findViewById(R.id.teamsListView);
        final View footer = getLayoutInflater().inflate(R.layout.teams_listview_footer, null);
        teamsListView.addFooterView(footer);
        final ArrayList<Team> list = Team.getTeams();
        final TeamsAdapter adapter = new TeamsAdapter(this, R.layout.list_item_team, Team.getTeams());
        teamsListView.setAdapter(adapter);
        final Button addTeamButton = (Button) findViewById(R.id.addTeamButton);
        addTeamButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateTeamActivity.class);
                startActivity(intent);
            }
        });
    }



    public void fillRightMenu() {
        if (Team.getCurrent() != null) {
            final ImageView currentTeamBadgeImageView = (ImageView) findViewById(R.id.currentTeamBadgeImageView);
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
            ImageLoader.getInstance().displayImage(Team.getCurrent().getBadgeURL(), currentTeamBadgeImageView, options);
            final TextView currentTeamTextView = (TextView) findViewById(R.id.currentTeamTextView);
            currentTeamTextView.setText(Team.getCurrent().getName());
        }
        final TextView membersNumberTextView = (TextView) findViewById(R.id.membersNumberTextView);
        membersNumberTextView.setText("Players(" + Integer.toString(Member.getMembers().size()) + ")");
        final ListView membersListview = (ListView) findViewById(R.id.membersListView);
        final ArrayList<Member> list = Member.getMembers();
        final MembersAdapter adapter = new MembersAdapter(this, R.layout.list_item_team_member, Member.getMembers());
        membersListview.setAdapter(adapter);
    }



    public void openLeftMenu(View view) {
        slidingMenu.showMenu();
    }



    public void openRightMenu(View view) {
        slidingMenu.showSecondaryMenu();
    }



//region ChatFragment Methods
    public void showMessage(QBChatMessage message) {
        if (chatFragment != null) {
            chatFragment.showMessage(message);
        }
    }



    public Uri getAttachmentUri() {
        return chatFragment == null ? null : chatFragment.getAttachmentUri();
    }


    public String getAttachmentQBId() {
        return chatFragment == null ? null : chatFragment.getAttachmentQBId();
    }



    public void clearAttachment() {
        if (chatFragment != null) {
            chatFragment.clearAttachment();
        }
    }



    public void refreshChat() {
        if (tabHost.getCurrentTab() == CHAT_TAB_NUMBER && chatFragment != null) {
            chatFragment.joinGroupChat();
        }
    }
//endregion

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == Consts.RESULT_LOAD_IMG) {
                if (chatFragment != null) {
                    chatFragment.attachmentAdded(data);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
}
