package com.ateamo.UI;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ateamo.ateamo.R;
import com.ateamo.core.Event;
import com.ateamo.core.Schedule;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AteamoMapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {
    private static final String EVENT_POSITION_ID = "eventPosition";

    private Event event;


    public static AteamoMapFragment newInstance(int eventPosition) {
        AteamoMapFragment fragment = new AteamoMapFragment();
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
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng eventLocation = new LatLng(event.getVenue().getLatitude(), event.getVenue().getLongitude());
        googleMap.addMarker(new MarkerOptions().position(eventLocation).title(event.getVenue().getName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null)
            getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
    }
}
