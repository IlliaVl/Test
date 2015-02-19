package com.ateamo.ateamo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vlasovia on 19.02.15.
 */
interface CallBack {
    void requestResponse(JSONObject response);
}



public class AteamoFetcher {

    public static final AteamoFetcher sharedInstance = new AteamoFetcher();

    static final String baseUrlString = "https://api.ateamo.com";
    static final String  authUrlString = "/oauth/token";
    static final String  teamsUrlString = "/teams";
    static final String  membersUrlString = "/members";
    static final String  clientSecret = "Wt}{tS{KG{U<uP&Iz],$,O6>*pG`NdP9]^#/bTkj5i($s!|PuVcKA0B!x-86~KUV";

    static final String  ACCESS_TOKEN_FIELD_ID = "access_token";
    static final String  EXPIRES_IN_FIELD_ID = "expires_in";
    static final String  REFRESH_TOKEN_FIELD_ID = "refresh_token";

    private Context context;

    private String accessToken = "";
    private Integer expiresIn = 0;
    private String refreshToken = "";


    void login(Context context, String username, String password, final CallBack loggedInCallback) {
        this.context = context;
        RequestParams params = new RequestParams();
        params.put("grant_type", "password");
        params.put("client_id", "ios");
        params.put("client_secret", clientSecret);
        params.put("username", username);
        params.put("password", password);
//        params.put("username", "iliavl@list.ru");
//        params.put("password", "p@ssword");
//        client.post(urlString, params, new JsonHttpResponseHandler()   
        AteamoRestClient.post(authUrlString, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                        JSONObject arr = new JSONObject(new String(byte));
                String decodedData = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(decodedData);
                    loggedInCallback.requestResponse(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                JSONObject arr = new JSONObject(new String(responseBody));
                Log.d("Response", decodedData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }



    private void saveAuthData(JSONObject jsonData) {
        try {
            accessToken = jsonData.getString(ACCESS_TOKEN_FIELD_ID);
            expiresIn = jsonData.getInt(EXPIRES_IN_FIELD_ID);
            refreshToken = jsonData.getString(REFRESH_TOKEN_FIELD_ID);
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(ACCESS_TOKEN_FIELD_ID, accessToken);
            editor.putInt(EXPIRES_IN_FIELD_ID, expiresIn);
            editor.putString(REFRESH_TOKEN_FIELD_ID, refreshToken);
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
