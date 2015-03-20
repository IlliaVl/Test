package com.ateamo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ateamo.ateamo.R;
import com.ateamo.core.Event;

import java.util.ArrayList;

/**
 * Created by vlasovia on 19.03.15.
 */
public class EventActionsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Action> actions;



    public EventActionsAdapter(Context context, Event event) {
        inflater = LayoutInflater.from(context);
        actions = Action.initActions(event);
    }



    @Override
    public int getCount() {
        return actions.size();
    }



    @Override
    public Object getItem(int position) {
        return actions.get(position);
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }



    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Action action = Action.getActions().get(position);
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_event_action, parent, false);
        }

        ImageView eventActionImageView = (ImageView) view.findViewById(R.id.eventActionImageView);
        eventActionImageView.setImageDrawable(action.getDrawable());

        TextView eventActionTitleTextView = (TextView) view.findViewById(R.id.eventActionTitleTextView);
        eventActionTitleTextView.setText(action.getTitle());

        TextView eventActionSummaryTextView = (TextView) view.findViewById(R.id.eventActionSummaryTextView);
        eventActionSummaryTextView.setText(action.getSummary());

        return view;
    }
}



