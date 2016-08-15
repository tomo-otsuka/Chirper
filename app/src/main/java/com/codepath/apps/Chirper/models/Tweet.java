package com.codepath.apps.Chirper.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tweet {

    private String text;
    private String createdAt;
    private long networkId;
    private User user;

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public long getNetworkId() {
        return networkId;
    }

    public User getUser() {
        return user;
    }

    public Tweet(JSONObject jsonObject) throws JSONException {
        text = jsonObject.getString("text");
        createdAt = jsonObject.getString("created_at");
        networkId = jsonObject.getLong("id");
        user = new User(jsonObject.getJSONObject("user"));
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray array) {
        ArrayList<Tweet> results = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new Tweet(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
