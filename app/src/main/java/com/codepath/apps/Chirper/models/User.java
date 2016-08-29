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

@Table(name = "user")
@Parcel(analyze = {User.class})
public class User extends Model {

    @Column(name = "name")
    public String name;

    @Column(name = "screenName")
    public String screenName;

    @Column(name = "profileImageUrl")
    public String profileImageUrl;

    @Column(name = "profileBackgroundImageUrl")
    public String profileBackgroundImageUrl;

    @Column(name = "networkId")
    public long networkId;

    @Column(name = "bio")
    public String bio;

    @Column(name = "followersCount")
    public int followersCount;

    @Column(name = "followingCount")
    public int followingCount;

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public long getNetworkId() {
        return networkId;
    }

    public String getBio() {
        return bio;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public User() { super(); }

    public User(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        screenName = jsonObject.getString("screen_name");
        profileImageUrl = jsonObject.getString("profile_image_url");
        profileBackgroundImageUrl = jsonObject.getString("profile_background_image_url");
        networkId = jsonObject.getLong("id");
        bio = jsonObject.getString("description");
        followersCount = jsonObject.getInt("followers_count");
        followingCount = jsonObject.getInt("friends_count");
    }

    public static ArrayList<User> fromJSONArray(JSONArray array) {
        ArrayList<User> results = new ArrayList<>();
        for (int x = 0; x < array.length(); x++) {
            try {
                results.add(new User(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public User getOrCreate() {
        From query = new Select().from(User.class).where("networkId = ?", getNetworkId());
        if (!query.exists()) {
            save();
            return this;
        } else {
            return query.executeSingle();
        }
    }

    public static User byId(long id) {
        return new Select().from(User.class).where("id = ?", id).executeSingle();
    }

    public static User byNetworkId(long networkId) {
        return new Select().from(User.class).where("networkId = ?", networkId).executeSingle();
    }
}
