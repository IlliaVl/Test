package com.ateamo.ateamo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImageLoader();
        initMenus();
    }



    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }



    private void initMenus() {
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


}
