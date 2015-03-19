package com.ateamo.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vlasovia on 14.03.15.
 */
public class Event {
//    private static final String _ID = "";
    private static final String DATE_ID = "date";//json object
    private static final String UTC_ID = "utc";
    private static final String EVENT_NAME_ID = "event_name";
    private static final String HOME_ID = "home";//json object
    private static final String TEAM_ID = "id";
    private static final String NAME_ID = "name";
    private static final String BADGE_ID = "badge";
    private static final String VENUE_ID = "venue";//json object
    private static final String VISITOR_ID = "visitor";//json object

    private Date date;
    private String dateString;
    private String dateStringShort;
    private String eventName;
    private Team home;
    private Team visitor;
    private Venue venue;
    private static ArrayList<Member> rsvps = new ArrayList<Member>();


    Event(JSONObject eventJSONObject) {
        try {
            JSONObject dateJsonObject = eventJSONObject.getJSONObject(DATE_ID);
            dateString = dateJsonObject.getString(UTC_ID);//"2010-10-15T09:27:37Z";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            try {
                date = simpleDateFormat.parse(dateString);
                simpleDateFormat = new SimpleDateFormat("h:mm a");
                dateStringShort = simpleDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            eventName = eventJSONObject.getString(EVENT_NAME_ID);
            home = getTeam(eventJSONObject, HOME_ID);
            visitor = getTeam(eventJSONObject, VISITOR_ID);
            venue = new Venue(eventJSONObject.getJSONObject(VENUE_ID));
//            JSONObject homeJsonObject = eventJSONObject.getJSONObject(HOME_ID);
//            String teamHash = homeJsonObject.getString(TEAM_ID);
//            Team team = Team.getTeamsMap().get(teamHash);
//            if (team == null) {
//
//            }
//
//            JSONObject visitorJsonObject = eventJSONObject.getJSONObject(VISITOR_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        try {
//            hash = teamJSONObject.get
//            name = teamJSONObject.getString(NAME_ID);
//            badgeURL = teamJSONObject.getString(BADGE_URL_ID);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }



    private Team getTeam(JSONObject jsonObject, String jsonObjectId) {
        Team team = null;
        JSONObject teamJsonObject = null;
        try {
            teamJsonObject = jsonObject.getJSONObject(jsonObjectId);
            String teamHash = teamJsonObject.getString(TEAM_ID);
            team = Team.getTeamsMap().get(teamHash);
            if (team == null) {
                team = new Team(teamHash, teamJsonObject.getString(NAME_ID), teamJsonObject.getString(BADGE_ID));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return team;
    }



    public Date getDate() {
        return date;
    }

//    dates=(
//
//    {
//        date = "2015-03-22";
//        events = (
//                {
//                        date = {
//                                local = "22-Mar-2015 12:00:00";
//        utc = "22-Mar-2015 05:00:00";
//        };
//        "event_date" = "2015-03-22";
//        "event_grp_id" = 3;
//        "event_hash" = "C8CBCAD3-F3D1-4979-B81EA6AFE041C9D5";
//        "event_id" = 39218;
//        "event_name" = "<null>";
//        "event_status_cd" = ACTIVE;
//        "event_type" = "Season Match";
//        "event_type_cd" = MATCH;
//        hidden = show;
//        home = {
//                  badge = "http://cdn6.atimg.net/DB904AF35FAFB322.png";
//                  "event_cd" = HOME;
//                  "event_id" = 178649;
//                  id = DB904AF35FAFB322;
//                  name = "Austin Athletic Club II";
//                  "numeric_id" = 767;
//                  "register_id" = 63873;
//                  "status_cd" = ACTIVE;
//        };
//        "member_id" = 31349;
//        "mgr_grp_id" = 767;
//        "period_id" = 84;
//        score = "<null>";
//        "status_cd" = ACTIVE;
//        venue = {
//                  address = "5600 East William Cannon, Austin, Texas 78744";
//                  city = "";
//                  id = 356 a192b7913b04c54574d18c28d46e6395428ab;
//                  lat = "30.1760617";
//                  lon = "-97.7436417";
//                  name = "Onion Creek Soccer Complex - OCSC 6";
//                  "postal_cd" = "";
//                  "short_name" = "OCSC 6";
//                  state = "";
//        };
//        visitor = {
//                  badge = "http://cdn6.atimg.net/20A1E2952D2F17C1.png";
//                  "event_cd" = VISITOR;
//                  id = 20 A1E2952D2F17C1;
//                  name = "Waterloo Natives";
//                  "numeric_id" = 1358;
//                  "register_id" = 63753;
//                  score = "<null>";
//                  "status_cd" = ACTIVE;
//        };
//        },
//        {
//            date = {
//                    local = "22-Mar-2015 02:00:00";
//            utc = "22-Mar-2015 07:00:00";
//            };
//            "event_date" = "2015-03-22";
//            "event_grp_id" = 3;
//            "event_hash" = "C8CBCBE9-CB7B-8DD5-9300318512B32605";
//            "event_id" = 39236;
//            "event_name" = "<null>";
//            "event_status_cd" = ACTIVE;
//            "event_type" = "Season Match";
//            "event_type_cd" = MATCH;
//            hidden = show;
//            home = {
//                    badge = "http://cdn6.atimg.net/221D48F4CE6443D9.png";
//            "event_cd" = VISITOR;
//            "event_id" = 178704;
//            id = 221D 48F 4 CE6443D9;
//            name = "Austin Athletic Club O30";
//            "numeric_id" = 1188;
//            "register_id" = 63874;
//            "status_cd" = ACTIVE;
//            };
//            "member_id" = 31349;
//            "mgr_grp_id" = 1188;
//            "period_id" = 84;
//            score = "<null>";
//            "status_cd" = ACTIVE;
//            venue = {
//                    address = "18701 Blake Manor Rd., Manor TX  78653 ";
//            city = Manor;
//            id = 1 b6453892473a467d07372d45eb05abc2031647a;
//            lat = "30.28371";
//            lon = "-97.52190";
//            name = "East Metropolitan Park - EMP 1";
//            "postal_cd" = 78653;
//            "short_name" = "EMP 1";
//            state = TX;
//            };
//            visitor = {
//                    badge = "http://cdn6.atimg.net/3F0718D1DA22686A.png";
//            "event_cd" = HOME;
//            id = 3F 0718D 1D A22686A;
//            name = Beercelona;
//            "numeric_id" = 1195;
//            "register_id" = 63870;
//            score = "<null>";
//            "status_cd" = ACTIVE;
//            };
//        }
//        );
//    }
//
//    ,


    public String getEventName() {
        return eventName;
    }

    public Team getHome() {
        return home;
    }

    public Team getVisitor() {
        return visitor;
    }

    public static ArrayList<Member> getRsvps() {
        return rsvps;
    }

    public Venue getVenue() {
        return venue;
    }

    public String getDateString() {
        return dateString;
    }

    public String getDateStringShort() {
        return dateStringShort;
    }
}



