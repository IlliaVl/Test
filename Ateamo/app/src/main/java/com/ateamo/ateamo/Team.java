package com.ateamo.ateamo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vlasovia on 20.02.15.
 */
public class Team {
    private static final String HASH_ID = "grp_hash";
    private static final String NAME_ID = "grp_name";
    private static final String BADGE_URL_ID = "grp_badge";

    static ArrayList<Team> teams = new ArrayList<Team>();

    private String hash;
    private String name;
    private String badgeURL;



    Team(JSONObject teamJSONObject) {
        try {
            hash = teamJSONObject.getString(HASH_ID);
            name = teamJSONObject.getString(NAME_ID);
            badgeURL = teamJSONObject.getString(BADGE_URL_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    static void fill(JSONArray teamsJSONArray) {
        for (int index = 0; index < teamsJSONArray.length(); ++index) {
            try {
                JSONObject teamJSONObject = teamsJSONArray.getJSONObject(index);
                teams.add(new Team(teamsJSONArray.getJSONObject(index)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getBadgeURL() {
        return badgeURL;
    }
}
