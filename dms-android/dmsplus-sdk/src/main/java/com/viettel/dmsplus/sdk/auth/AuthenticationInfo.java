package com.viettel.dmsplus.sdk.auth;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.dmsplus.sdk.models.UserInfo;

/**
 * Object holding authentication info.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationInfo implements Parcelable {

    public static final Parcelable.Creator<AuthenticationInfo> CREATOR = new Parcelable.Creator<AuthenticationInfo>() {
        public AuthenticationInfo createFromParcel(Parcel in) {
            return new AuthenticationInfo(in);
        }

        public AuthenticationInfo[] newArray(int size) {
            return new AuthenticationInfo[size];
        }
    };

    @JsonProperty("refresh_time")
    private Long refreshTime;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    private String scope;

    @JsonProperty("token_type")
    private String tokenType;

    private UserInfo userInfo;

    public AuthenticationInfo() {

    }

    public AuthenticationInfo(Parcel in) {
        refreshTime = in.readLong();
        if (refreshTime == 0) {
            refreshTime = null;
        }
        clientId = in.readString();
        accessToken = in.readString();
        refreshToken = in.readString();
        expiresIn = in.readLong();
        if (expiresIn == 0) {
            expiresIn = null;
        }
        userInfo = in.readParcelable(this.getClass().getClassLoader());
        scope = in.readString();
        tokenType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(refreshTime == null? 0 : refreshTime);
        dest.writeString(clientId);
        dest.writeString(accessToken);
        dest.writeString(refreshToken);
        dest.writeLong(expiresIn == null ? 0 : expiresIn);
        dest.writeParcelable(userInfo, flags);
        dest.writeString(scope);
        dest.writeString(tokenType);
    }

    /**
     * Creates a clone of a AuthenticationInfo object.
     *
     * @return  clone of AuthenticationInfo object.
     */
    public AuthenticationInfo clone() {
        AuthenticationInfo cloned = new AuthenticationInfo();
        cloneInfo(cloned, this);
        return cloned;
    }

    /**
     * Clone AuthenticationInfo from source object into target object. Note that this method assumes the two objects have same userInfo.
     * Otherwise it would not make sense to do a clone operation.
     */
    public static void cloneInfo(AuthenticationInfo targetInfo, AuthenticationInfo sourceInfo) {
        targetInfo.setAccessToken(sourceInfo.getAccessToken());
        targetInfo.setRefreshToken(sourceInfo.getRefreshToken());
        targetInfo.setRefreshTime(sourceInfo.getRefreshTime());
        targetInfo.setClientId(sourceInfo.getClientId());
        targetInfo.setScope(sourceInfo.getScope());
        targetInfo.setTokenType(sourceInfo.getTokenType());
        targetInfo.setExpiresIn(sourceInfo.getExpiresIn());
        if (targetInfo.getUserInfo() == null) {
            targetInfo.setUserInfo(sourceInfo.getUserInfo());
        }
    }

    /**
     * Wipe out all the information in this object.
     */
    public void wipeOutAuth() {
        setUserInfo(null);
        setClientId(null);
        setAccessToken(null);
        setRefreshToken(null);
    }

    public Long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }


    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
