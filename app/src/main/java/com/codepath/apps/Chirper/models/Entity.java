package com.codepath.apps.Chirper.models;

import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Transient;

import java.util.ArrayList;
import java.util.List;

@Table(name = "entity")
@Parcel(analyze = {Entity.class})
public class Entity extends Model {

    @Transient
    private TableInfo mTableInfo;

    @Column(name = "networkId")
    public long networkId;

    @Column(name = "url")
    public String url;

    @Column(name = "type")
    public String type;

    @Column(name = "tweet")
    public Tweet tweet;

    @Column(name = "user")
    public User user;

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public long getNetworkId() {
        return networkId;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public Entity() {
        super();
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

    public Long saveIfNew() {
        if (!new Select().from(Entity.class).where("networkId = ?", getNetworkId()).exists()) {
            return save();
        }
        return null;
    }

    public static List<Entity> getByTweet(Tweet tweet) {
        return new Select().from(Entity.class).where("tweet = ?", tweet.getId()).execute();
    }

    public static List<Entity> getByUserId(long userId) {
        return new Select().from(Entity.class).where("user = ?", userId)
                .orderBy("id DESC")
                .limit(25)
                .execute();
    }

    public static List<Entity> getByUserId(long userId, Long maxId) {
        return new Select().from(Entity.class).where("user = ? AND networkId < ?", userId, maxId)
                .orderBy("networkId DESC")
                .limit(25)
                .execute();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
