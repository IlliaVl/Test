package com.ateamo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ateamo.ateamo.R;
import com.ateamo.core.Event;
import com.ateamo.core.Schedule;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by vlasovia on 17.03.15.
 */
public class ScheduleAdapter extends BaseAdapter {
    private static String TAG = "SCHEDULE ADAPTER";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private LayoutInflater inflater;
    private ArrayList<Event> schedule;
    private ArrayList<String> datesStrings;
    private TreeSet sectionIndices;



    public ScheduleAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        update();
    }


    public void update() {
        schedule = Schedule.getSchedule();
        datesStrings = Schedule.getDatesStrings();
        sectionIndices = Schedule.getIndices();
    }



    @Override
    public int getItemViewType(int position) {
        return sectionIndices.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }



    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @Override
    public int getCount() {
        if (schedule != null) {
//            return schedule.size() + (sectionIndices == null ? 0 : sectionIndices.size());
            //if schedule array has null items as blank sections for list view
            return schedule.size();
        } else {
            return 0;
        }
    }



    @Override
    public Event getItem(int position) {
        if (schedule != null) {
            return schedule.get(position);
        } else {
            return null;
        }
    }



    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.i(TAG, "Position: " + position);
        int type = getItemViewType(position);
        Log.i(TAG, "Type: " + type);
        switch (type) {
            case TYPE_ITEM:
                EventViewHolder eventViewHolder;
                if (view == null) {
                    view = inflater.inflate(R.layout.list_item_event, parent, false);
                    eventViewHolder = createEventViewHolder(view);
                    view.setTag(eventViewHolder);
                } else {
                    eventViewHolder = (EventViewHolder) view.getTag();
                }
                Log.i(TAG, "Date string: " + schedule.get(position).getDateStringShort());
                eventViewHolder.timeTextView.setText(schedule.get(position).getDateStringShort());
                eventViewHolder.opponentNameTextView.setText(schedule.get(position).getVisitor().getName());
                eventViewHolder.venueTextView.setText(schedule.get(position).getVenue().getName());
                break;
            case TYPE_SEPARATOR:
                SectionViewHolder sectionViewHolder;
                if (view == null) {
                    view = inflater.inflate(R.layout.list_item_event_sector, parent, false);
                    sectionViewHolder = createSectionViewHolder(view);
                    view.setTag(sectionViewHolder);
                } else {
                    sectionViewHolder = (SectionViewHolder) view.getTag();
                }
                sectionViewHolder.scheduleSectorTextView.setText(datesStrings.get(position));
                break;
        }


        return view;
    }



    private SectionViewHolder createSectionViewHolder(View v) {
        SectionViewHolder holder = new SectionViewHolder();
        holder.scheduleSectorTextView = (TextView) v.findViewById(R.id.scheduleSectorTextView);
        return holder;
    }



    private static class SectionViewHolder {
        TextView scheduleSectorTextView;
    }



    private EventViewHolder createEventViewHolder(View v) {
        EventViewHolder holder = new EventViewHolder();
        holder.timeTextView = (TextView) v.findViewById(R.id.timeTextView);
        holder.rsvpsTextView = (TextView) v.findViewById(R.id.rsvpsTextView);
        holder.opponentNameTextView = (TextView) v.findViewById(R.id.opponentNameTextView);
        holder.venueTextView = (TextView) v.findViewById(R.id.venueTextView);
        return holder;
    }



    private static class EventViewHolder {
        TextView timeTextView;
        TextView rsvpsTextView;
        TextView opponentNameTextView;
        TextView venueTextView;
    }
}
