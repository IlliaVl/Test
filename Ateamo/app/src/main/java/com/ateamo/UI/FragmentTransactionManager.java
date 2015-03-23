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



    public void push(Fragment fragment, String fragmentId, Fragment parent, FragmentManager fragmentManager) {
        ArrayList<FragmentWithId> fragmentsArray = fragmentsMap.get(parent);
        if (fragmentsArray == null) {
            fragmentsArray = new ArrayList<>();
            fragmentsMap.put(parent, fragmentsArray);
        }
        if (fragment != null) {
            fragmentsArray.add(new FragmentWithId(fragmentId, fragment));
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.realtabcontent, fragment, fragmentId);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }


    public boolean pop(Fragment parent) {
        ArrayList<FragmentWithId> fragmentsArray = fragmentsMap.get(parent);
        if (fragmentsArray != null && fragmentsArray.size() > 0) {
            fragmentsArray.remove(fragmentsArray.size() - 1);
            return true;
        }
        return false;
    }


    public void performTransaction(Fragment parent, String id, FragmentManager fragmentManager) {
        for(int index = 0; index < fragmentManager.getBackStackEntryCount(); ++index) {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.realtabcontent, parent, id);
        fragmentTransaction.commit();
        ArrayList<FragmentWithId> fragmentsArray = fragmentsMap.get(parent);
        if (fragmentsArray != null && fragmentsArray.size() > 0) {
            for (int index = 0; index < fragmentsArray.size(); ++index) {
                fragmentTransaction = fragmentManager.beginTransaction();
                FragmentWithId fragmentWithId = fragmentsArray.get(index);
                fragmentTransaction.replace(R.id.realtabcontent, fragmentWithId.getFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
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
