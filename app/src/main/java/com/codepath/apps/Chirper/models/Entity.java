package com.codepath.apps.Chirper.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Entity {

    private long networkId;
    private String url;
    private String type;

    public long getNetworkId() {
        return networkId;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public Entity(JSONObject jsonObject) {
        url = jsonObject.optString("media_url");
        networkId = jsonObject.optLong("id");
        type = jsonObject.optString("type");
    }

    public static ArrayList<Entity> fromJSONArray(JSONArray array) {
        ArrayList<Entity> results = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new Entity(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
