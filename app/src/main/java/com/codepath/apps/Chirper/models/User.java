package com.codepath.apps.Chirper.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String name;
    private String screenName;
    private String profileImageUrl;
    private long networkId;

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public long getNetworkId() {
        return networkId;
    }

    public User(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        screenName = jsonObject.getString("screen_name");
        profileImageUrl = jsonObject.getString("profile_image_url");
        networkId = jsonObject.getLong("id");
    }
}
