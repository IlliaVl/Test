package com.ateamo.ateamo;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by vlasovia on 21.02.15.
 */
public class TabContent implements TabHost.TabContentFactory {
    private Context mContext;

    public TabContent(Context context) {
        mContext = context;
    }

    @Override
    public View createTabContent(String tag) {
        View v = new View(mContext);
        return v;
    }
}