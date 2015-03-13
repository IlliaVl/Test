package com.ateamo.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.ateamo.UI.MainActivity;
import com.ateamo.ateamo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vlasovia on 21.02.15.
 */
public class Member implements Serializable {
    private static final String HASH_ID = "member_hash";
    private static final String NAME_ID = "display_name";
    private static final String EMAIL_ID = "email";
    private static final String PROFILE_PICTURE_ID = "profile_img";

    private static ArrayList<Member> members = new ArrayList<Member>();
    private static Member current;

    private static Context context;

    private String hash;
    private String name;
    private String email;
    private String profilePictureURL;


    public Member() {
        hash = "c02e33a07f2811e4b5c1001851012600";
        name = "Illia Vlasov";
        email = "iliavl@list.ru";
        profilePictureURL = "https://5f31800e1d2ac4a222ba-0da610d65cf2689f3fa9d0c4703b3fef.ssl.cf1.rackcdn.com/397ff121-ab0c-379b-44edef72e85eb137.jpg";

//        hash = "b8ddb9307f2911e4b5c1001851012600";
//        name = "Sheldon Cooper";
//        email = "vkfriendslocator@gmail.com";
//        profilePictureURL = "https://5f31800e1d2ac4a222ba-0da610d65cf2689f3fa9d0c4703b3fef.ssl.cf1.rackcdn.com/3b667168-cb7a-6a7a-305623ccab9e15b5.jpg";
    }


    public Member(String hash, String name, String email, String profilePictureURL) {
        this.hash = hash;
        this.name = name;
        this.email = email;
        this.profilePictureURL = profilePictureURL;
    }



    Member(JSONObject memberJSONObject) {
        try {
            hash = memberJSONObject.getString(HASH_ID);
            name = memberJSONObject.getString(NAME_ID);
            profilePictureURL = memberJSONObject.getString(PROFILE_PICTURE_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    static void fill(JSONArray membersJSONArray) {
        members.clear();
        for (int index = 0; index < membersJSONArray.length(); ++index) {
            try {
                JSONObject memberJSONObject = membersJSONArray.getJSONObject(index);
                members.add(new Member(membersJSONArray.getJSONObject(index)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().fillRightMenu();
        }
    }



    static public void fillTest() {
        members.clear();
        String hash = "c02e33a07f2811e4b5c1001851012600";
        String name = "Illia Vlasov";
        String email = "iliavl@list.ru";
        String profilePictureURL = "https://5f31800e1d2ac4a222ba-0da610d65cf2689f3fa9d0c4703b3fef.ssl.cf1.rackcdn.com/397ff121-ab0c-379b-44edef72e85eb137.jpg";
        add(hash, name, email, profilePictureURL);
        hash = "c02e33a07f2811001851012600";
        name = "Mish Ding";
        email = "mishding@gmail.com";
        profilePictureURL = "https://5f31800e1d2ac4a222ba-0da610d65cf2689f3fa9d0c4703b3fef.ssl.cf1.rackcdn.com/d54e0d73-9dc8-5369-8c048f9878054fbb.png";
        add(hash, name, email, profilePictureURL);
        hash = "b8ddb9307f2911e4b5c1001851012600";
        name = "Sheldon Cooper";
        email = "hh@gmail.com";
        profilePictureURL = "https://5f31800e1d2ac4a222ba-0da610d65cf2689f3fa9d0c4703b3fef.ssl.cf1.rackcdn.com/3b667168-cb7a-6a7a-305623ccab9e15b5.jpg";
        add(hash, name, email, profilePictureURL);
        add(hash, name, email, profilePictureURL);
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().fillRightMenu();
        }
    }



    static private void add(String hash, String name, String email, String profilePictureURL) {
        Member member = new Member(hash, name, email, profilePictureURL);
        members.add(member);
    }



    static public void initCurrentMember(Context context) {
        Member.context = context;
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (sharedPref.getString(HASH_ID, null) != null) {
            setCurrent(new Member(sharedPref.getString(HASH_ID, null), sharedPref.getString(NAME_ID, null), sharedPref.getString(EMAIL_ID, null), sharedPref.getString(PROFILE_PICTURE_ID, null)));
        }
    }



    static private void saveCurrentMember() {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(HASH_ID, current.hash);
        editor.putString(NAME_ID, current.name);
        editor.putString(EMAIL_ID, current.email);
        editor.putString(PROFILE_PICTURE_ID, current.profilePictureURL);
        editor.commit();

    }



    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public static void setCurrent(Member current) {
        //TODO Заменить на нормальную работу после завершения сервера
        if (Member.current != null) {
            return;
        }
        Member.current = current;
        saveCurrentMember();
        AteamoFetcher.getSharedInstance().loadTeams();
        QBHelper.getSharedInstance().login();
    }

    public static Member getCurrent() {
        return current;
    }

    public static ArrayList<Member> getMembers() {
        return members;
    }
}
