package com.ateamo.UI;

import android.os.Bundle;

/**
 * Created by vlasovia on 20.03.15.
 */
public interface OnSelectedItemListener {
    public static final String FRAGMENT_TYPE_PARAMETER_ID = "FragmentType";
    public static final String POSITION_PARAMETER_ID = "position";
    public static final String EVENT_POSITION_PARAMETER_ID = "eventPosition";

    public enum FragmentType {
        SCHEDULE,
        EVENT
    }
    public void onEventSelected(Bundle parametersBundle);
//    public void onEventSelected(int position, FragmentType fragmentType);
}
