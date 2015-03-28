package com.ateamo.core;

import com.ateamo.UI.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

/**
 * Created by vlasovia on 14.03.15.
 */
public class Schedule {
    private static final String DATES_ID = "dates";//json object
    private static final String DATE_ID = "date";
    private static final String EVENTS_ID = "events";//json object

    //    private static HashMap<String, Event> teams = new HashMap<String, Event>();
    private static ArrayList<Event> schedule = new ArrayList<Event>();
    private static ArrayList<Date> dates = new ArrayList<Date>();
    private static ArrayList<String> datesStrings = new ArrayList<String>();
    private static TreeSet indices = new TreeSet();


    static void fill(JSONObject jsonObject) {
        clear();
        if (jsonObject != null) {
            try {
                JSONArray datesJsonArray = jsonObject.getJSONArray(DATES_ID);
                for (int index = 0; index < datesJsonArray.length(); ++index) {
                    parseEvents(datesJsonArray.getJSONObject(index));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        MainActivity mainActivity = MainActivity.getInstance();
        if (mainActivity != null) {
            mainActivity.updateSchedule();
        }
    }


    private static void clear() {
        schedule.clear();
        dates.clear();
        datesStrings.clear();
        indices.clear();
    }


    private static void parseEvents(JSONObject dateJsonObject) {
        try {
            String dateString = dateJsonObject.getString(DATE_ID);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = format.parse(dateString);
                dates.add(date);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d");
                datesStrings.add(simpleDateFormat.format(date));
                indices.add(schedule.size());//if schedule array has null items as blank sections for list view
//                indices.add(schedule.size() + indices.size());
                schedule.add(null);//if schedule array has null items as blank sections for list view
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONArray eventsJsonArray = dateJsonObject.getJSONArray(EVENTS_ID);
            for (int index = 0; index < eventsJsonArray.length(); ++index) {
                JSONObject eventJSONObject = eventsJsonArray.getJSONObject(index);
                Event event = new Event(eventsJsonArray.getJSONObject(index));
                schedule.add(event);
                datesStrings.add(null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<Event> getSchedule() {
        return schedule;
    }


    public static ArrayList<Date> getDates() {
        return dates;
    }


    public static TreeSet getIndices() {
//        int[] indices = new int[Schedule.indices.size()];
//        for (int i = 0; i < indices.length; i++) {
//            indices[i] = Schedule.indices.get(i).intValue();
//        }
        return indices;
    }

    public static ArrayList<String> getDatesStrings() {
        return datesStrings;
    }
}
