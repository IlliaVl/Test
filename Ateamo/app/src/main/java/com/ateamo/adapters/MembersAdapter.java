package com.ateamo.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ateamo.UI.MainActivity;
import com.ateamo.ateamo.R;
import com.ateamo.core.Member;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by vlasovia on 25.02.15.
 */
public class MembersAdapter extends ArrayAdapter<Member> {

    private MainActivity mainActivity;

    public MembersAdapter(Context context, int resource, ArrayList<Member> objects) {
        super(context, resource, objects);
        mainActivity = (MainActivity) context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mainActivity.getLayoutInflater().inflate(R.layout.list_item_team_member, parent, false);
        }
        Member member = getItem(position);

        ImageView memberImageView = (ImageView)convertView.findViewById(R.id.memberImageView);
        ImageLoader.getInstance().displayImage(member.getProfilePictureURL(), memberImageView);

        TextView memberNameTextView = (TextView)convertView.findViewById(R.id.memberNameTextView);
        memberNameTextView.setText(member.getName());

        TextView memberEmailTextView = (TextView)convertView.findViewById(R.id.memberEmailTextView);
        memberEmailTextView.setText(member.getEmail());

        return convertView;
    }

}
