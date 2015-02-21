package com.ateamo.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.ateamo.ateamo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vlasovia on 21.02.15.
 */
public class Member {
    private static final String HASH_ID = "hash";
    private static final String NAME_ID = "name";
    private static final String PROFILE_PICTURE_ID = "img";

    private static ArrayList<Member> members = new ArrayList<Member>();
    private static Member current = new Member();

    private String hash;
    private String name;
    private String profilePictureURL;


    public Member() {
    }



    Member(JSONObject teamJSONObject) {
        try {
            hash = teamJSONObject.getString(HASH_ID);
            name = teamJSONObject.getString(NAME_ID);
            profilePictureURL = teamJSONObject.getString(PROFILE_PICTURE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    static void fill(JSONArray membersJSONArray) {
        for (int index = 0; index < membersJSONArray.length(); ++index) {
            try {
                JSONObject memberJSONObject = membersJSONArray.getJSONObject(index);
                members.add(new Member(membersJSONArray.getJSONObject(index)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    static public void initCurrentMember(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        current.hash = sharedPref.getString(HASH_ID, null);
        if (current.hash != null) {
            current.name = sharedPref.getString(NAME_ID, null);
            current.profilePictureURL = sharedPref.getString(PROFILE_PICTURE_ID, null);
        }
    }



    public String getHash() {
        return hash;
    }

    public static Member getCurrent() {
        return current;
    }
}
