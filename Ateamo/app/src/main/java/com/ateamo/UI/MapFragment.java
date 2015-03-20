package com.ateamo.UI;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ateamo.ateamo.R;
import com.ateamo.core.Event;
import com.ateamo.core.Schedule;

public class MapFragment extends Fragment {
    private static final String EVENT_POSITION_ID = "eventPosition";

    private Event event;



    public static MapFragment newInstance(int eventPosition) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(EVENT_POSITION_ID, eventPosition);
        fragment.setArguments(args);
        return fragment;
    }



    public MapFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


}
