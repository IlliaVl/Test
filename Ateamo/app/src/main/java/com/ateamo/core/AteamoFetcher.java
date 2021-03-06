package com.ateamo.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.ateamo.UI.MainActivity;
import com.ateamo.ateamo.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vlasovia on 19.02.15.
 */
public class AteamoFetcher {
    static final String TAG = "ATEAMO RESPONSE: ";

    private static final AteamoFetcher sharedInstance = new AteamoFetcher();

    static final String baseUrlString = "https://api.ateamo.com";
    static final String authUrlString = "/oauth/token";
    static final String teamsUrlString = "/teams";
    static final String membersUrlString = "/members";
    static final String scheduleUrlString = "/schedule";
    static final String clientSecret = "Wt}{tS{KG{U<uP&Iz],$,O6>*pG`NdP9]^#/bTkj5i($s!|PuVcKA0B!x-86~KUV";

    static final String ACCESS_TOKEN_FIELD_ID = "access_token";
    static final String EXPIRES_IN_FIELD_ID = "expires_in";
    static final String REFRESH_TOKEN_FIELD_ID = "refresh_token";

    private Context context;

    private String accessToken = "";
    private Integer expiresIn = 0;
    private String refreshToken = "";

    private CallBack callback;



    public void init(Context context) {
        this.context = context;
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        accessToken = sharedPref.getString(ACCESS_TOKEN_FIELD_ID, null);
        if (accessToken != null) {
            expiresIn = sharedPref.getInt(EXPIRES_IN_FIELD_ID, 0);
            refreshToken = sharedPref.getString(REFRESH_TOKEN_FIELD_ID, null);
        }
    }



    public void login(Context context, String username, String password, final CallBack loggedInCallback) {
        this.context = context;
        callback = loggedInCallback;
        RequestParams params = new RequestParams();
        params.put("grant_type", "password");
        params.put("client_id", "ios");
        params.put("client_secret", clientSecret);
//        params.put("username", username);
//        params.put("password", password);
        params.put("username", "iliavl@list.ru");
        params.put("password", "p@ssword");
        AteamoRestClient.post(authUrlString, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String decodedData = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(decodedData);
//                    loggedInCallback.requestResponse(jsonObject);
                    saveAuthData(jsonObject);
                    //TODO Заменить на нормальную работу после завершения сервера
                    if (Member.getCurrent() == null) {
                        Member.setCurrent(new Member());
                    } else {
                        if (callback != null) {
                            callback.requestResponse(null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, decodedData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, error.getLocalizedMessage());
                if (MainActivity.getInstance() != null){
                    new AlertDialog.Builder(AteamoFetcher.this.context).setMessage("Ateamo login failed. Error: " + error.getLocalizedMessage()).create().show();
                }
                //TODO Заменить на нормальную работу после завершения сервера
                Member.setCurrent(new Member());
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



    public void loadTeams() {
        Header[] headers = {new BasicHeader("Authorization", "Bearer " + accessToken)};
        AteamoRestClient.get(context, teamsUrlString, headers, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String decodedData = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(decodedData);
                    Team.fill(jsonArray);
                    if (callback != null) {
                        callback.requestResponse(null);
                    }
//                    loadSchedule();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, error.getLocalizedMessage());
                if (MainActivity.getInstance() != null){
                    new AlertDialog.Builder(MainActivity.getInstance()).setMessage("Teams not loaded. Error: " + error.getLocalizedMessage()).create().show();
                }
                //TODO Заменить на нормальную работу после завершения сервера
                Team.fill(null);
                if (callback != null) {
                    callback.requestResponse(null);
                }
            }
        });
    }



    public void loadSchedule(boolean currentTeamSchedule) {
        Team currentTeam = Team.getCurrent();
        if (currentTeamSchedule && currentTeam == null) {
            return;
        }
        Header[] headers = {new BasicHeader("Authorization", "Bearer " + accessToken)};
        RequestParams params = new RequestParams();
        if (currentTeamSchedule) {
            params.put("grp", currentTeam.getHash());
        }
        AteamoRestClient.get(context, scheduleUrlString, headers, params, new AsyncHttpResponseHandler() {//new RequestParams("grp", currentTeam.getHash())
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String decodedData = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(decodedData);
                    Schedule.fill(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, error.getLocalizedMessage());
                if (MainActivity.getInstance() != null){
                    new AlertDialog.Builder(MainActivity.getInstance()).setMessage("Teams not loaded. Error: " + error.getLocalizedMessage()).create().show();
                }
                //TODO Заменить на нормальную работу после завершения сервера
            }
        });
    }



    public void loadMembers() {
        Team currentTeam = Team.getCurrent();
        if (currentTeam == null) {
            return;
        }
        Header[] headers = {new BasicHeader("Authorization", "Bearer " + accessToken)};
        AteamoRestClient.get(context, membersUrlString, headers, new RequestParams("grp", currentTeam.getHash()), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String decodedData = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(decodedData);
                    Member.fill(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, error.getLocalizedMessage());
                if (MainActivity.getInstance() != null){
                    new AlertDialog.Builder(MainActivity.getInstance()).setMessage("Teams not loaded. Error: " + error.getLocalizedMessage()).create().show();
                }
                //TODO Заменить на нормальную работу после завершения сервера
                new PostTask().execute("");
            }
        });
    }



    private class PostTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "All done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Member.fillTest();
        }
    }

    public static AteamoFetcher getSharedInstance() {
        return sharedInstance;
    }
}
