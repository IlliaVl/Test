package com.ateamo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ateamo.ateamo.R;
import com.ateamo.core.Event;
import com.ateamo.core.Schedule;

import java.util.ArrayList;
import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by vlasovia on 14.03.15.
 */
public class StickyScheduleAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

    private LayoutInflater inflater;
    private ArrayList<Event> schedule;
    private int[] sectionIndices;



    public StickyScheduleAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        schedule = Schedule.getSchedule();
//        sectionIndices = Schedule.getIndices();
    }



    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_event_sector, viewGroup, false);
            holder = createHeaderViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }
        String headerText = "September";
        holder.scheduleHeaderTextView.setText(headerText);
        return view;
    }



    @Override
    public long getHeaderId(int i) {
        return 2;
    }



    @Override
    public int getCount() {
        if (schedule != null) {
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
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_event, parent, false);
            holder = createViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String headerText = schedule.get(position).getDate().toString();
        holder.eventNameTextView.setText(headerText);
        return view;
    }



    @Override
    public int getPositionForSection(int section) {
        if (sectionIndices.length == 0) {
            return 0;
        }

        if (section >= sectionIndices.length) {
            section = sectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return sectionIndices[section];
    }



    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < sectionIndices.length; i++) {
            if (position < sectionIndices[i]) {
                return i - 1;
            }
        }
        return sectionIndices.length - 1;
    }



    @Override
    public Object[] getSections() {
        Date[] dates = Schedule.getDates().toArray(new Date[Schedule.getDates().size()]);
        return dates;
    }



    private HeaderViewHolder createHeaderViewHolder(View v) {
        HeaderViewHolder holder = new HeaderViewHolder();
        holder.scheduleHeaderTextView = (TextView) v.findViewById(R.id.scheduleSectorTextView);
        return holder;
    }



    private static class HeaderViewHolder {
        TextView scheduleHeaderTextView;
    }



    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.eventNameTextView = (TextView) v.findViewById(R.id.opponentNameTextView);
        return holder;
    }



    private static class ViewHolder {
        TextView eventNameTextView;
    }

}
