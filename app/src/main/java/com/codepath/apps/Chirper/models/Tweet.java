package com.codepath.apps.Chirper.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Table(name = "tweet")
@Parcel(analyze = {Tweet.class})
public class Tweet extends Model {

    @Column(name = "text")
    public String text;

    @Column(name = "createdAt")
    public String createdAt;

    @Column(name = "networkId")
    public long networkId;

    @Column(name = "user")
    public User user;

    public ArrayList<Entity> entities;

    @Column(name = "liked")
    public Boolean liked;

    @Column(name = "likeCount")
    public long likeCount;

    @Column(name = "retweeted")
    public Boolean retweeted;

    @Column(name = "retweetCount")
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

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public void setRetweetCount(long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public Tweet() { super(); }

    public Tweet(JSONObject jsonObject) throws JSONException {
        text = jsonObject.getString("text");
        createdAt = jsonObject.getString("created_at");
        networkId = jsonObject.getLong("id");
        user = new User(jsonObject.getJSONObject("user"));
        user = user.getOrCreate();

        entities = new ArrayList<>();
        try {
            entities.addAll(Entity.fromJSONArray(jsonObject.getJSONObject("entities").getJSONArray("media")));
            for (Entity entity : entities) {
                entity.setTweet(this);
                entity.setUser(user);
            }
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

    public Long saveIfNew() {
        if (!new Select().from(Tweet.class).where("networkId = ?", getNetworkId()).exists()) {
            Long res = save();
            for (Entity entity : entities) {
                entity.saveIfNew();
            }
            return res;
        }
        return null;
    }

    public static List<Tweet> recentItems() {
        List<Tweet> tweets = new Select().from(Tweet.class).orderBy("createdAt DESC").limit("300").execute();
        for (Tweet tweet : tweets) {
            tweet.entities = new ArrayList<Entity>();
            tweet.entities.addAll(Entity.getByTweet(tweet));
        }
        return tweets;
    }

    public Tweet refresh() {
        From query = new Select().from(Tweet.class).where("networkId = ?", getNetworkId());
        if (!query.exists()) {
            return this;
        } else {
            Tweet tweet = query.executeSingle();
            tweet.entities = new ArrayList<>();
            tweet.entities.addAll(Entity.getByTweet(tweet));
            return tweet;
        }
    }
}
