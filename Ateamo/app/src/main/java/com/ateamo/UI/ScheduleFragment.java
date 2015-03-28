package com.ateamo.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ateamo.adapters.ScheduleAdapter;
import com.ateamo.ateamo.R;


public class ScheduleFragment extends BaseEventFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        type = OnSelectedItemListener.FragmentType.SCHEDULE;
        viewId = R.layout.fragment_schedule;
        listViewId = R.id.scheduleListView;
        listAdapter = new ScheduleAdapter(getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public void update() {
        ((ScheduleAdapter) listAdapter).update();
        listAdapter.notifyDataSetChanged();
    }
}
