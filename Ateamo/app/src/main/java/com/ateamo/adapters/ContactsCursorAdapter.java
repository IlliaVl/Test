package com.ateamo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.ateamo.ateamo.R;

/**
 * Created by vlasovia on 27.03.15.
 */
public class ContactsCursorAdapter extends CursorAdapter {

    private LayoutInflater layoutInflater;



    public ContactsCursorAdapter(Context context) {
        super(context, null, 0);
        layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return layoutInflater.inflate(R.layout.list_item_contact, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView contactMainTextView = (TextView) view.findViewById(R.id.contactMainTextView);
        String text = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
        contactMainTextView.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
//        TextView contactSecondaryTextView = (TextView) view.findViewById(R.id.contactSecondaryTextView);
//        contactMainTextView.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
    }
}
