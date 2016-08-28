package com.codepath.apps.Chirper.models;

import com.activeandroid.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel(analyze = {DirectMessage.class})
public class DirectMessage extends Model {

    long networkId;
    User recipient;
    User sender;
    String createdAt;
    String text;

    public long getNetworkId() {
        return networkId;
    }

    public User getRecipient() {
        return recipient;
    }

    public User getSender() {
        return sender;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }

    public DirectMessage() {}

    public DirectMessage(JSONObject jsonObject) throws JSONException {
        networkId = jsonObject.getLong("id");
        recipient = new User(jsonObject.getJSONObject("recipient"));
        sender = new User(jsonObject.getJSONObject("sender"));
        createdAt = jsonObject.getString("created_at");
        text = jsonObject.getString("text");
    }

    public static ArrayList<DirectMessage> fromJSONArray(JSONArray array) {
        ArrayList<DirectMessage> results = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new DirectMessage(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
