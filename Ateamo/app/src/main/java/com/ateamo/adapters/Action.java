package com.ateamo.adapters;

import android.graphics.drawable.Drawable;

import com.ateamo.UI.ApplicationSingleton;
import com.ateamo.ateamo.R;
import com.ateamo.core.Event;

import java.util.ArrayList;

public class Action {
    public enum ActionType {
        VIEW_RSVPS,
        GET_DIRECTION,
        SEND_A_REMINDER,
        WETHER_FORECAST,
        CALL_CAPTAIN
    }

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
