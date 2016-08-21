package com.codepath.apps.Chirper.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Table(name = "user")
@Parcel(analyze = {User.class})
public class User extends Model {

    @Column(name = "name")
    public String name;

    @Column(name = "screenName")
    public String screenName;

    @Column(name = "profileImageUrl")
    public String profileImageUrl;

    @Column(name = "networkId")
    public long networkId;

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

    public User() { super(); }

    public User(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString("name");
        screenName = jsonObject.getString("screen_name");
        profileImageUrl = jsonObject.getString("profile_image_url");
        networkId = jsonObject.getLong("id");
    }

    public static User byId(long id) {
        return new Select().from(User.class).where("id = ?", id).executeSingle();
    }

    public static User byNetworkId(long networkId) {
        return new Select().from(User.class).where("networkId = ?", networkId).executeSingle();
    }
}
