package com.ateamo.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.ateamo.adapters.Action;
import com.ateamo.adapters.MembersAdapter;
import com.ateamo.adapters.TeamsAdapter;
import com.ateamo.ateamo.R;
import com.ateamo.core.Member;
import com.ateamo.core.QBHelper;
import com.ateamo.core.Team;
import com.ateamo.definitions.Consts;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class MainActivity extends FragmentActivity implements OnSelectedItemListener {
    private static MainActivity instance = null;

    private final int SCHEDULE_TAB_NUMBER = 0;
    private final int CHAT_TAB_NUMBER = 1;

    private final String EVENT_FRAGMENT_ID = "EventFragment";
    private final String MAP_FRAGMENT_ID = "MapFragment";
    private final String SCHEDULE_TAB_ID = "schedule";
    private final String SCHEDULE_TAB_NAME_ID = "Schedule";
    private final String NOTIFICATIONS_TAB_ID = "notifications";
    private final String NOTIFICATIONS_TAB_NAME_ID = "Notifications";
    private final String CHAT_TAB_ID = "chat";
    private final String CHAT_TAB_NAME_ID = "Chat";
    private final String PAYMENTS_TAB_ID = "payments";
    private final String PAYMENTS_TAB_NAME_ID = "Payments";

    private SlidingMenu slidingMenu;
    private ScheduleFragment scheduleFragment;
    private NotificationsFragment notificationsFragment;
    private ChatFragment chatFragment;
    private PaymentsFragment paymentsFragment;
    private Fragment currentParentFragment;
    private TabHost tabHost;

    private TextView currentTeamNameTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        currentTeamNameTextView = (TextView) findViewById(R.id.currentTeamNameTextView);
        currentTeamNameTextView.setText(Team.getCurrent() == null ? "" : Team.getCurrent().getName());
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
                FragmentTransactionManager fragmentTransactionManager = FragmentTransactionManager.getInstance();
                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                scheduleFragment = (ScheduleFragment) fragmentManager.findFragmentByTag(SCHEDULE_TAB_ID);
                notificationsFragment = (NotificationsFragment) fragmentManager.findFragmentByTag(NOTIFICATIONS_TAB_ID);
                chatFragment = (ChatFragment) fragmentManager.findFragmentByTag(CHAT_TAB_ID);
                paymentsFragment = (PaymentsFragment) fragmentManager.findFragmentByTag(PAYMENTS_TAB_ID);
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                String id = SCHEDULE_TAB_ID;
                if(tabId.equalsIgnoreCase(SCHEDULE_TAB_ID)) {
                    if(scheduleFragment == null){
                        scheduleFragment = new ScheduleFragment();
                    }
                    currentParentFragment = scheduleFragment;
                } else if (tabId.equalsIgnoreCase(NOTIFICATIONS_TAB_ID)) {
                    if(notificationsFragment == null) {
                        notificationsFragment = new NotificationsFragment();
                    }
                    currentParentFragment = notificationsFragment;
                    id = NOTIFICATIONS_TAB_ID;
                } else if (tabId.equalsIgnoreCase(CHAT_TAB_ID)) {
                    if(chatFragment == null) {
                        chatFragment = new ChatFragment();
                        chatFragment.setDialog(QBHelper.getSharedInstance().getCurrentTeamDialog());
                    }
                    currentParentFragment = chatFragment;
                    id = CHAT_TAB_ID;
                } else if (tabId.equalsIgnoreCase(PAYMENTS_TAB_ID)) {
                    if(paymentsFragment == null) {
                        paymentsFragment = new PaymentsFragment();
                    }
                    currentParentFragment = paymentsFragment;
                    id = PAYMENTS_TAB_ID;
                }
                fragmentTransactionManager.push(null, null, currentParentFragment, fragmentManager);
                fragmentTransactionManager.performTransaction(currentParentFragment, id, fragmentManager);
//                fragmentTransaction.replace(R.id.realtabcontent, fragment, id);
//                fragmentTransaction.commit();
            }
        };
        tabHost.setOnTabChangedListener(tabChangeListener);
        tabHost.addTab(getTabSpec(tabHost.newTabSpec(SCHEDULE_TAB_ID), SCHEDULE_TAB_NAME_ID, R.drawable.schedule_icon));
        tabHost.addTab(getTabSpec(tabHost.newTabSpec(NOTIFICATIONS_TAB_ID), NOTIFICATIONS_TAB_NAME_ID, R.drawable.notifications_icon));
        tabHost.addTab(getTabSpec(tabHost.newTabSpec(CHAT_TAB_ID), CHAT_TAB_NAME_ID, R.drawable.chat_icon));
        tabHost.addTab(getTabSpec(tabHost.newTabSpec(PAYMENTS_TAB_ID), PAYMENTS_TAB_NAME_ID, R.drawable.payments_icon));
    }



    private TabHost.TabSpec getTabSpec(TabHost.TabSpec tabSpec, String tabName, int tabIcon) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        view.setBackgroundResource(R.drawable.tab_indicator);
        ((TextView)view.findViewById(R.id.tabTextView)).setText(tabName);
        ((ImageView)view.findViewById(R.id.tabImageView)).setBackgroundResource(tabIcon);
        tabSpec.setContent(new TabContent(getBaseContext()));
        tabSpec.setIndicator(view);
        return tabSpec;
    }



    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }


//region Menu Methods
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



//region LeftMenu Methods
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
        final Button addTeamButton = (Button) findViewById(R.id.addTeamButton);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateTeamActivity.class);
                startActivity(intent);
            }
        });
        final TeamsAdapter adapter = new TeamsAdapter(this, R.layout.list_item_team, Team.getTeams());
        int currentTeamIndex = Team.getTeams().indexOf(Team.getCurrent());
        adapter.setSelectedItemPosition(currentTeamIndex);
        teamsListView.setAdapter(adapter);
        teamsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                Team team = Team.getTeams().get(position);
                Team.setCurrent(team);
                slidingMenu.showContent();
                adapter.setSelectedItemPosition(position);
            }
        });
    }



    public void openLeftMenu(View view) {
        slidingMenu.showMenu();
    }
//endregion



//region RightMenu Methods
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
        final MembersAdapter adapter = new MembersAdapter(this, R.layout.list_item_team_member, Member.getMembers());
        membersListview.setAdapter(adapter);
        membersListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                Member member = Member.getMembers().get(position);
                Intent intent = new Intent(MainActivity.this, PrivateChatActivity.class);
                intent.putExtra(Consts.OPPONENT_EXTRA_ID, member);
                MainActivity.this.startActivity(intent);
            }
        });
    }



    public void openRightMenu(View view) {
        slidingMenu.showSecondaryMenu();
    }
//endregion
//endregion



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
        if (chatFragment != null) {
            chatFragment.setDialog(QBHelper.getSharedInstance().getCurrentTeamDialog());
            if (tabHost.getCurrentTab() == CHAT_TAB_NUMBER) {
                chatFragment.joinChat();
            }
        }
    }
//endregion



    public static MainActivity getInstance() {
        return instance;
    }



    public void currentTeamChanged(Team team) {
        currentTeamNameTextView.setText(team.getName());
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



    @Override
    public void onEventSelected(Bundle parametersBundle) {
        FragmentType fragmentType = FragmentType.values()[parametersBundle.getInt(OnSelectedItemListener.FRAGMENT_TYPE_PARAMETER_ID)];
        int position = parametersBundle.getInt(OnSelectedItemListener.POSITION_PARAMETER_ID);
        Fragment fragment = null;
        String fragmentId = "";
        switch (fragmentType) {
            case SCHEDULE:
                fragment = EventFragment.newInstance(position);
                fragmentId = EVENT_FRAGMENT_ID;
                break;
            case EVENT:
                Action.ActionType actionType = Action.ActionType.values()[position];
                switch (actionType) {
                    case GET_DIRECTION:
                        int eventPosition = parametersBundle.getInt(OnSelectedItemListener.EVENT_POSITION_PARAMETER_ID);
                        fragment = AteamoMapFragment.newInstance(eventPosition);
                        fragmentId = MAP_FRAGMENT_ID;
                        break;
                    default:
                        return;
                }
                break;
        }
        FragmentTransactionManager.getInstance().push(fragment, fragmentId, currentParentFragment, getSupportFragmentManager());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentTransactionManager.getInstance().pop(currentParentFragment);
    }
}
