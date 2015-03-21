package com.ateamo.UI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ateamo.ateamo.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vlasovia on 21.03.15.
 */
public class FragmentTransactionManager {
    static private FragmentTransactionManager instance = new FragmentTransactionManager();

    private HashMap<Fragment, ArrayList<FragmentWithId>> fragmentsMap = new HashMap<>();
    private ArrayList<FragmentWithId> currentFragments = null;



    public void push(Fragment fragment, String fragmentId, Fragment parent, FragmentManager fragmentManager) {
        ArrayList<FragmentWithId> fragmentsArray = fragmentsMap.get(parent);
        if (fragmentsArray == null) {
            fragmentsArray = new ArrayList<>();
            fragmentsMap.put(parent, fragmentsArray);
        }
        if (fragment != null) {
            fragmentsArray.add(new FragmentWithId(fragmentId, fragment));
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.realtabcontent, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    public void pop(Fragment parent) {
        ArrayList<FragmentWithId> fragmentsArray = fragmentsMap.get(parent);
        if (fragmentsArray != null && fragmentsArray.size() > 0) {
            fragmentsArray.remove(fragmentsArray.size() - 1);
        }
    }


    public void performTransaction(Fragment parent, String id, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.realtabcontent, parent, id);
        ArrayList<FragmentWithId> fragmentsArray = fragmentsMap.get(parent);
        if (fragmentsArray != null && fragmentsArray.size() > 0) {
            if (currentFragments != null) {
                for (int index = 0; index < currentFragments.size(); ++index) {
                    FragmentWithId fragmentWithId = currentFragments.get(index);
                    fragmentTransaction.detach(fragmentWithId.getFragment());
                }
            }
            currentFragments = fragmentsArray;
            for (int index = 0; index < fragmentsArray.size(); ++index) {
                FragmentWithId fragmentWithId = fragmentsArray.get(index);
                fragmentTransaction.replace(R.id.realtabcontent, fragmentWithId.getFragment());
                fragmentTransaction.addToBackStack(null);
            }
        }
        fragmentTransaction.commit();
    }


    private FragmentTransactionManager() {
    }

    public static FragmentTransactionManager getInstance() {
        return instance;
    }
}



class FragmentWithId {
    private String id;
    private Fragment fragment;

    FragmentWithId(String id, Fragment fragment) {
        this.id = id;
        this.fragment = fragment;
    }


    public String getId() {
        return id;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
