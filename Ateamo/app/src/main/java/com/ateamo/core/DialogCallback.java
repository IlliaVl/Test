package com.ateamo.core;

import com.quickblox.chat.model.QBDialog;

/**
 * Created by vlasovia on 07.03.15.
 */
public interface DialogCallback {
    void response(QBDialog dialog);
}
