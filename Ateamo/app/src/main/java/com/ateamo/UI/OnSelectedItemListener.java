package com.ateamo.UI;

/**
 * Created by vlasovia on 20.03.15.
 */
public interface OnSelectedItemListener {
    public enum FragmentType {
        SCHEDULE,
        EVENT
    }
    public void onEventSelected(int position, FragmentType fragmentType);
}
