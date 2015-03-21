package com.ateamo.UI;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
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
    Parcelable state;

    private OnSelectedItemListener selectedItemListener;
    protected OnSelectedItemListener.FragmentType type;
    protected int viewId;
    protected int listViewId;
    private ListView listView;
    protected BaseAdapter listAdapter;
    protected Bundle parametersBundle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parametersBundle = new Bundle();
        View view = inflater.inflate(viewId, container, false);
        listView = (ListView) view.findViewById(listViewId);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parametersBundle.clear();
                itemSelected(position);
            }
        });
        if(state != null) {
            listView.onRestoreInstanceState(state);
            state = null;
        }
        return view;
    }


    protected void itemSelected(int position) {
        if (selectedItemListener != null) {
            parametersBundle.putInt(OnSelectedItemListener.POSITION_PARAMETER_ID, position);
            parametersBundle.putInt(OnSelectedItemListener.FRAGMENT_TYPE_PARAMETER_ID, type.ordinal());
            selectedItemListener.onEventSelected(parametersBundle);
        }
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


    @Override
    public void onPause() {
        state = listView.onSaveInstanceState();
        super.onPause();
    }
}
