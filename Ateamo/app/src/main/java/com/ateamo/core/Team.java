package com.ateamo.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.ateamo.UI.ApplicationSingleton;
import com.ateamo.UI.MainActivity;
import com.ateamo.ateamo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vlasovia on 20.02.15.
 */
public class Team {
    private static final String HASH_ID = "grp_hash";
    private static final String NAME_ID = "grp_name";
    private static final String BADGE_URL_ID = "grp_badge";

    private static HashMap<String, Team> teams = new HashMap<String, Team>();
    private static ArrayList<Team> teamsList = new ArrayList<Team>();
    private static Team current;

    private String hash;
    private String name;
    private String badgeURL;



    Team() {
    }



    Team(JSONObject teamJSONObject) {
        try {
            hash = teamJSONObject.getString(HASH_ID);
            name = teamJSONObject.getString(NAME_ID);
            badgeURL = teamJSONObject.getString(BADGE_URL_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    Team(String hash, String name, String badgeURL) {
        this.hash = hash;
        this.name = name;
        this.badgeURL = badgeURL;
    }



    static void fill(JSONArray teamsJSONArray) {
        //TODO Заменить на нормальную работу после завершения сервера. teamsJSONArray != null - передаем null, ибо не залогинены в Ateamo
        if (teamsJSONArray != null) {
            for (int index = 0; index < teamsJSONArray.length(); ++index) {
                try {
                    JSONObject teamJSONObject = teamsJSONArray.getJSONObject(index);
                    Team team = new Team(teamsJSONArray.getJSONObject(index));
                    teams.put(team.hash, team);
                    teamsList.add(team);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setCurrent(getDefaultTeam(teamsList.get(0)));
        } else {//TODO Заменить на нормальную работу после завершения сервера
            fill();
        }
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().fillLeftMenu();
            MainActivity.getInstance().fillRightMenu();
        }
    }



    static void fill() {
        Team team = new Team();
        team.hash = "B815676B-DBAF-82D2-081A16F4832C3E91";
        team.name = "Newtone Fake";
        team.badgeURL = "https://56cca85f3d3b04107041-73eb90caa3bcecdbd63d0f38a250333c.ssl.cf1.rackcdn.com/B815676B-DBAF-82D2-081A16F4832C3E91.png";
        teams.put(team.hash, team);
        teamsList.add(team);
        team = new Team();
        team.hash = "ABF297A1-CECD-0D94-5E31209D9ECE2C9C";
        team.name = "Rabbit Fake";
        team.badgeURL = "https://56cca85f3d3b04107041-73eb90caa3bcecdbd63d0f38a250333c.ssl.cf1.rackcdn.com/ABF297A1-CECD-0D94-5E31209D9ECE2C9C.png";
        teams.put(team.hash, team);
        teamsList.add(team);
        team = new Team();
        team.hash = "DB904AF35FAFB322";
        team.name = "Austin Athletic Club II Fake";
        team.badgeURL = "http://cdn6.atimg.net/DB904AF35FAFB322.png";
        teams.put(team.hash, team);
        teamsList.add(team);
        team = new Team();
        team.hash = "221D48F4CE6443D9";
        team.name = "Austin Athletic Club O30 Fake";
        team.badgeURL = "http://cdn6.atimg.net/221D48F4CE6443D9.png";
        teams.put(team.hash, team);
        teamsList.add(team);

        setCurrent(getDefaultTeam(teamsList.get(0)));
    }



    private static Team getDefaultTeam(Team defaultTeam) {
        Context context = ApplicationSingleton.getInstance();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String defaultTeamHash = sharedPref.getString(HASH_ID, null);
        if (defaultTeamHash != null) {
            defaultTeam = teams.get(defaultTeamHash);
        }
        return defaultTeam;
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

    public static HashMap<String, Team> getTeamsMap() {
        return teams;
    }

    public static ArrayList<Team> getTeams() {
        return teamsList;
    }

    public static void setCurrent(Team current) {
        Team.current = current;
        AteamoFetcher.getSharedInstance().loadMembers();
        AteamoFetcher.getSharedInstance().loadSchedule(true);
        QBHelper.getSharedInstance().loginToCurrentTeamChat();
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().currentTeamChanged(current);
        }
        Context context = ApplicationSingleton.getInstance();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(HASH_ID, current.hash);
        editor.commit();
    }

    public static Team getCurrent() {
        return current;
    }
}
