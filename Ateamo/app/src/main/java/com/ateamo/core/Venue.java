package com.ateamo.core;

import org.json.JSONException;
import org.json.JSONObject;

public class Venue {
    private static String NAME_ID = "name";
    private static String ADDRESS_ID = "address";
    private static String LATITUDE_ID = "lat";
    private static String LONGITUDE_ID = "lon";

    private String name;
    private String address;
    private double latitude;
    private double longitude;

    Venue(JSONObject jsonObject) {
        Team team = null;
        JSONObject teamJsonObject = null;
        try {
            name = jsonObject.getString(NAME_ID);
            address = jsonObject.getString(ADDRESS_ID);
            latitude = jsonObject.getDouble(LATITUDE_ID);
            longitude = jsonObject.getDouble(LONGITUDE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
