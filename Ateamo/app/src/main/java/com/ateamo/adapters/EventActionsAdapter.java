package com.ateamo.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ateamo.UI.ApplicationSingleton;
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



class Action {
    private static ArrayList<Action> actions = new ArrayList<>();

    private Drawable drawable;
    private String title;
    private String summary;



    public static ArrayList<Action> initActions(Event event) {
        actions.clear();
        actions.add(new Action(R.drawable.checkmark_black, "View RSVPs", "Number of RSVPs"));
        actions.add(new Action(R.drawable.map, "Get Directions", event.getVenue().getAddress()));
        actions.add(new Action(R.drawable.notification, "Send a Reminder", "666 people have not responded"));
        actions.add(new Action(R.drawable.weather_sunny, "Weather Forecast", "Forecast available 7 days prior to event"));
        actions.add(new Action(R.drawable.call_person, "Call your Captain", ""));
//        NSComparisonResult result;
//        result = [eventDate compare:maxDate];
//        if(result==NSOrderedAscending) {
//            cell.eventActionImage.image = [UIImage imageNamed:@"weather-sunny"];
//            cell.eventActionSummary.text = @"See Game Day Forecast";
//        } else {
//            cell.eventActionImage.image = [UIImage imageNamed:@"weather-sunny"];
//            cell.eventActionSummary.text = @"Forecast available 7 days prior to event";
//            cell.userInteractionEnabled = NO;
//        }
        return actions;
    }



    Action(int imageResource, String title, String summary) {
        this.title = title;
        this.summary = summary;
        drawable = ApplicationSingleton.getInstance().getResources().getDrawable(imageResource);
    }



    public static ArrayList<Action> getActions() {
        return actions;
    }


    public Drawable getDrawable() {
        return drawable;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }
}
