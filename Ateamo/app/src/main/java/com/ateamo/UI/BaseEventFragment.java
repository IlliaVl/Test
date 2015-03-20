package com.ateamo.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by vlasovia on 20.03.15.
 */
public class BaseEventFragment extends Fragment {

    private OnSelectedItemListener selectedItemListener;
    protected OnSelectedItemListener.FragmentType type;
    protected int viewId;
    protected int listViewId;
    protected BaseAdapter listAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(viewId, container, false);
        ListView listView = (ListView) view.findViewById(listViewId);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedItemListener != null) {
                    selectedItemListener.onEventSelected(position, type);
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            selectedItemListener = (OnSelectedItemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        selectedItemListener = null;
    }
}
