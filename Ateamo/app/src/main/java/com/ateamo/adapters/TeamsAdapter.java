package com.ateamo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ateamo.UI.MainActivity;
import com.ateamo.ateamo.R;
import com.ateamo.core.Team;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by vlasovia on 25.02.15.
 */
public class TeamsAdapter extends ArrayAdapter<Team> {
    private static String TAG = "TEAMS ADAPTER";

    private MainActivity mainActivity;
    private int selectedItemPosition = -1;



    public TeamsAdapter(Context context, int resource, ArrayList<Team> objects) {
        super(context, resource, objects);
        mainActivity = (MainActivity) context;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "Position: " + position);
        if(convertView == null) {
            convertView = mainActivity.getLayoutInflater().inflate(R.layout.list_item_team, parent, false);
        }
        Team team = getItem(position);

        TextView teamNameTextView = (TextView)convertView.findViewById(R.id.teamNameTextView);
        teamNameTextView.setText(team.getName());

        ImageView teamBadgeImageView = (ImageView)convertView.findViewById(R.id.teamBadgeImageView);
        ImageLoader.getInstance().displayImage(team.getBadgeURL(), teamBadgeImageView);

        if (position == selectedItemPosition) {
            convertView.setBackgroundColor(Color.GRAY);
        } else {
            convertView.setBackgroundColor(Color.BLACK);
        }

        return convertView;
    }



    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
        notifyDataSetChanged();
    }
}
