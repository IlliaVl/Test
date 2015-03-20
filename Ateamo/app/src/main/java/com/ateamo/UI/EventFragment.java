package com.ateamo.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ateamo.adapters.EventActionsAdapter;
import com.ateamo.ateamo.R;
import com.ateamo.core.Event;
import com.ateamo.core.Schedule;
import com.ateamo.core.Team;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EventFragment extends BaseEventFragment {
    private static final String EVENT_POSITION_ID = "eventPosition";

    private Event event;


    public static EventFragment newInstance(int eventPosition) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putInt(EVENT_POSITION_ID, eventPosition);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int eventPosition = getArguments().getInt(EVENT_POSITION_ID);
            event = Schedule.getSchedule().get(eventPosition);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        type = OnSelectedItemListener.FragmentType.EVENT;
        viewId = R.layout.fragment_event;
        listViewId = R.id.actionsListView;
        listAdapter = new EventActionsAdapter(getActivity(), event);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Date eventDate = event.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d");

        initTextView(view, R.id.eventDateTextView, simpleDateFormat.format(eventDate));

        initTextView(view, R.id.eventTimeTextView, event.getDateStringShort());

        initBadge(view, R.id.homeBadgeImageView, event.getHome());
        initBadge(view, R.id.visitorBadgeImageView, event.getVisitor());

        initTextView(view, R.id.locationEventTextView, event.getVenue().getAddress());

        initTextView(view, R.id.homeNameTextView, event.getHome().getName());

        initTextView(view, R.id.visitorNameTextView, event.getVisitor().getName());

        return view;
    }


    private void initBadge(View view, int id, Team team) {
        ImageView badgeImageView = (ImageView) view.findViewById(id);
        ImageLoader.getInstance().displayImage(team.getBadgeURL(), badgeImageView);
    }


    private void initTextView(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        textView.setText(text);
    }

}
