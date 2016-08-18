package com.codepath.apps.Chirper.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Tweet {

    public String text;
    public String createdAt;
    public long networkId;
    public User user;
    public ArrayList<Entity> entities;
    public Boolean liked;
    public long likeCount;
    public Boolean retweeted;
    public long retweetCount;

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

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Boolean getLiked() {
        return liked;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }


    public Tweet() {}

    public Tweet(JSONObject jsonObject) throws JSONException {
        text = jsonObject.getString("text");
        createdAt = jsonObject.getString("created_at");
        networkId = jsonObject.getLong("id");
        user = new User(jsonObject.getJSONObject("user"));

        entities = new ArrayList<>();
        try {
            entities.addAll(Entity.fromJSONArray(jsonObject.getJSONObject("entities").getJSONArray("media")));
        } catch (JSONException e) {}

        liked = jsonObject.getBoolean("favorited");
        likeCount = jsonObject.getLong("favorite_count");
        retweeted = jsonObject.getBoolean("retweeted");
        retweetCount = jsonObject.getLong("retweet_count");
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
